package com.tcc.renxl.recover;

import com.alibaba.fastjson.JSONObject;
import com.tcc.renxl.*;
import com.tcc.renxl.repository.TransactionRecoverRepository;
import com.tcc.renxl.util.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by changming.xie on 6/2/16.
 */
@Slf4j
@Data
public class RecoverScheduledJob implements InitializingBean {
    private int maxSleepTime;
    private int sleepTime = 1;
    private ThreadPoolExecutor threadPoolExecutor;
    // 获取一次需要处理的待恢复事务总数
    private long transactionNum;
    // 恢复事务dao层
    TransactionRecoverRepository transactionRecoverRepository;
    // 值表示分钟
    private int manualRecoveryMinute;

    // 值表示分钟
    private int manualRecoveryNumber;
    // 人工恢复接口
    private ManualRecovery manualRecoveryService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 最大线程数暂时不好估计;需要结合分布式事务的耗时评估出
        if (transactionRecoverRepository == null) {
            throw new RuntimeException("init {RecoverScheduledJob} fail: null point exception for transactionRecoverRepository");
        }
        if (threadPoolExecutor == null) {
            this.threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(2000));
        }
        if (transactionNum <= 0) {
            transactionNum = 10;
        }

        if (sleepTime <= 0) {
            sleepTime = 1;
        }
        if(maxSleepTime <= 0 ){
            maxSleepTime  = 300;
        }

        if(manualRecoveryMinute <=0){
            manualRecoveryMinute = 1*60*5;
        }
        if(manualRecoveryNumber <=0){
            manualRecoveryNumber = 100;
        }

        if(manualRecoveryService == null){
            manualRecoveryService = new DefaultManualRecovery();
        }

        // 剔除调度，采用自定义恢复结构加速恢复过程！
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    log.error("==========================================================sche1");
                    synchronized (Wait.Instance) {
                        List<RecoverTransactionInfo> recoverTransactions = transactionRecoverRepository.getRecoverTransactions(transactionNum);
                        if (!CollectionUtils.isEmpty(recoverTransactions)) {
                            log.error("==========================================================sche2");

                            try {
                                startScheduler(recoverTransactions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            log.error("==========================================================sche3");

                        } else {
                            log.error("==========================================================sche4");

                            //  阻塞 采用通知+ 自我释放
                            try {
                                // 等待阻塞 不参与cpu调度 释放独占锁 并且当前无其他线程占用该锁
                                Wait.Instance.wait(sleepTime * 1000);
                                if(log.isInfoEnabled()){
                                    log.info(" no  RecoverTransactionInfo need to do , its will be waiting for {} seconds",sleepTime );
                                }
                                // 如果是被唤醒,则sleepTime = 会被重置为1;
                                sleepTime =sleepTime >= 300 ? maxSleepTime: sleepTime*2;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 准备释放锁
                            log.info(" ready to release synchronized of Wait.Instance ");
                        }
                    }
                }
            }
        });
    }

    /**
     * 恢复事务的时候唤醒处理
     */
    public void notifyAwake(){
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (Wait.Instance) {
                    sleepTime = 1;
                    Wait.Instance.notifyAll();
                }
                }
        });
    }

    //
    private void startScheduler(List<RecoverTransactionInfo> recoverTransactions) {

        // 处理事务
        for (RecoverTransactionInfo transactionInfo : recoverTransactions) {
            try {
                // 获取业务信息
                BizMetadata metadata = transactionInfo.getMetadata();
                // 初始化事务第一次恢复时间
                transactionInfo.initFirstStartRecoverTime();
                //// 获取事务状态
                TransactionStatus transactionStatus = transactionInfo.getTransactionStatus();
                // 获取业务对象
                Class target = metadata.getTarget(); // TODO 可从spring容器获取
                Object targetObject = target.newInstance();
                Method invokeMethod = null;
                if (transactionStatus.getState() == Status.cancel) {
                    invokeMethod = targetObject.getClass().getMethod(metadata.getCancelMethodName(), metadata.getParams());
                }
                if (transactionStatus.getState() == Status.confirm) {
                    invokeMethod =  targetObject.getClass().getMethod(metadata.getConfirmmMethodName(), metadata.getParams());
                }


                try {
                    try {
                         for(Object obj:metadata.getParamsValues()){
                             System.out.println("======================================================================");
                             System.out.println(obj.getClass());
                             System.out.println(obj.getClass().getName());
                             System.err.println("======================================================================");

                         }
                        invokeMethod.invoke(targetObject, metadata.getParamsValues());
                        // 确认成功！删除存储的事务信息
                        transactionRecoverRepository.delete(transactionInfo);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new TransactionException();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new TransactionException();
                    }
                } catch (Throwable e) {
                    // 恢复失败
                    // 持久化
                    // 等待下次处理：[下次可能被发送到其他服务]
                    transactionInfo.addFailureTimes();
                    transactionRecoverRepository.save(transactionInfo);

                    if(transactionInfo.getFailureTimes() > manualRecoveryNumber){
                        if(log.isErrorEnabled()){
                            log.error("transactionInfo.failure number more than 100, please resolve recover transaction for {},{}",target.getName(), JSONObject.toJSONString(metadata.getParamsValues()));
                        }
                        manualRecoveryService.manualRecoveryForNumber(transactionInfo);
                    }

                    if( DateUtil.differentMinute(transactionInfo.getFirstStartRecoverTime(),new Date() )> manualRecoveryMinute ) {
                        if(log.isErrorEnabled()){
                            log.error("transactionInfo.failure time more than {} minute please resolve recover transaction for {},{}",manualRecoveryMinute,target.getName(), JSONObject.toJSONString(metadata.getParamsValues()));
                            manualRecoveryService.manualRecoveryForTime(transactionInfo);

                        }
                    }
                    e.printStackTrace();
                }

            } catch (InstantiationException e) {
                // 业务中try cancel confirm 配置错误或其他业务原因不做处理
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // 业务中try cancel confirm 配置错误或其他业务原因不做处理
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // 业务中try cancel confirm 配置错误或其他业务原因不做处理
                e.printStackTrace();
            }


        }
    }


}

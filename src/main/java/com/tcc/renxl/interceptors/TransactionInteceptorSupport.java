package com.tcc.renxl.interceptors;

import com.alibaba.fastjson.JSON;
import com.tcc.renxl.BizMetadata;
import com.tcc.renxl.RecoverTransactionInfo;
import com.tcc.renxl.TransactionInfo;
import com.tcc.renxl.TransactionManager;
import com.tcc.renxl.recover.RecoverScheduledJob;
import com.tcc.renxl.repository.TransactionRecoverRepository;
import com.tcc.renxl.repository.TransactionRepository;
import com.tcc.renxl.util.BizMetadataUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Serializable;
import java.lang.reflect.Method;

@Slf4j
@Data
public abstract class TransactionInteceptorSupport implements Serializable {


    protected TransactionManager transactionManager;
    protected RecoverScheduledJob recoverInterface;

//    单点事务的实现方式: PlatformTransactionManager
//    Object invokeWithinTransaction(Method method, Class targetClass) throws Throwable {
//        final TransactionAttribute txAttr = this.getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
//        final PlatformTransactionManager tm = this.determineTransactionManager(txAttr);
//        final String joinpointIdentification = this.methodIdentification(method, targetClass);
//            TransactionAspectSupport.TransactionInfo txInfo x= this.createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
//            Object retVal = null;
//
//            try {
//                retVal = invocation.proceedWithInvocation();
//            } catch (Throwable var15) {
//                this.completeTransactionAfterThrowing(txInfo, var15);
//                throw var15;
//            } finally {
//                this.cleanupTransactionInfo(txInfo);
//            }
//
//            this.commitTransactionAfterReturning(txInfo);
//            return retVal;
//        return null;
//    }




    // 分布式事务的实现方式
    protected Object invokeWithinTransaction(ProceedingJoinPoint pjp) throws Throwable {

        preTcc(); // TODO 在try之前增加一步嗅探能力，降低拜占庭问题后果导致崩溃的可能性
        Object returnValue = null;

        // 开始业务
        Method bizMethod = BizMetadataUtil.getMethod(pjp);// todo 部分参数序列化时会被干掉
        BizMetadata bizMetadata = new BizMetadata(bizMethod,pjp.getArgs());
        TransactionInfo transactionInfo = transactionManager.begin(null, bizMetadata);
        try {
            try {
                 returnValue = pjp.proceed();
                 // 区分出事务持久化异常和业务异常
                transactionManager.confirmTransactionStatus(transactionInfo);
                transactionManager.commit(transactionInfo);
            } catch (Throwable e) {
                log.error("分布式事务try阶段失败 transactionInfo => {},异常 =>{}", JSON.toJSONString(transactionInfo), e.getMessage());
                transactionManager.cancelTransactionStatus(transactionInfo);
                transactionManager.rollback(transactionInfo);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            // 删除原本事务 标记失败事务：用于区分查询 开始最终一致性调度
            TransactionRecoverRepository transactionRecoverRepository = recoverInterface.getTransactionRecoverRepository();
            TransactionRepository transactionRepository = transactionManager.getTransactionRepository();
            transactionRepository.delete(transactionInfo);
            RecoverTransactionInfo recoverTransactionInfo = new RecoverTransactionInfo();
            recoverTransactionInfo.setTransactionStatus(transactionInfo.getTransactionStatus());
            recoverTransactionInfo.setTramsactionId(transactionInfo.getTramsactionId());
            recoverTransactionInfo.setMetadata(transactionInfo.getMetadata());
            transactionRecoverRepository.save(recoverTransactionInfo);
            recoverInterface.notifyAwake();
        } finally {
            // 清除当前线程中可能存在的一些分布式事务信息
            transactionManager.clear(transactionInfo);
        }
        return returnValue;
     }

    /**
     * 嗅探处理 ：降低拜占庭问题的发生的可能性
     */
    protected abstract void preTcc();

}

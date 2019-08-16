package com.tcc.renxl;

import com.tcc.renxl.repository.TransactionRepository;
import com.tcc.renxl.util.IdUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 结合世界应用:目前只开发DB分布式事务管理器
 */
@Component
@Slf4j
@Data
public class TransactionManager extends AabstractTransactionManager implements InitializingBean {





    @Override
    public void afterPropertiesSet()   {
        if(transactionRepository == null){
            throw new RuntimeException("transactionRepository is null...");
        }
    }


    @Autowired
    private IdUtil idUtil;


    @Autowired
    private TransactionRepository transactionRepository;

    // 暂时不注入
    @Autowired
    @Lazy
    private DefaultTransactionAttributeProcess defaultTransactionAttributeProcess;
    @Override
    public TransactionInfo begin(TransactionInfo transactionInfo,BizMetadata  metadata) throws TransactionException {
        if(transactionInfo != null ){
            return defaultTransactionAttributeProcess.begin(transactionInfo);
        }
        TransactionInfo beginTransactionInfo = new TransactionInfo();
        String transactionId = idUtil.getNext();
        beginTransactionInfo.setTramsactionId(transactionId);
        beginTransactionInfo.setMetadata(metadata);
        TransactionStatus transactionStatus = new TransactionStatus();
        transactionStatus.setState(Status.trying);
        transactionStatus.setTransactionId(transactionId);
        beginTransactionInfo.setTransactionStatus(transactionStatus);
        // 持久化事务信息 :事务自身以及业务元数据
        transactionRepository.save(beginTransactionInfo);
        return beginTransactionInfo;
    }


    @Override
    public void commit(TransactionInfo transactionInfo) throws TransactionException {
        try {
            BizMetadata metadata = transactionInfo.getMetadata();
            Class target = metadata.getTarget();
            Object targetObject = target.newInstance();
            Class[] params = metadata.getParams();
            Method invokeMethod = target.getDeclaredMethod(metadata.getConfirmmMethodName(), params);
            invokeMethod.invoke(targetObject, metadata.getParamsValues());
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new TransactionException();

        }
    }

    @Override
    public void rollback(TransactionInfo transactionInfo) throws TransactionException {
        try {
            BizMetadata metadata = transactionInfo.getMetadata();
            Class target = metadata.getTarget();
            Object targetObject = target.newInstance();
            Method invokeMethod = target.getDeclaredMethod(metadata.getCancelMethodName(), metadata.getParams());
            Object[] paramsValues = metadata.getParamsValues();
            invokeMethod.invoke(targetObject, paramsValues);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new TransactionException();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new TransactionException();
        }


    }

    @Override
    public void clear(TransactionInfo transactionInfo) {
        transactionRepository.delete(transactionInfo);
        transactionInfo = null;
     }


    public void confirmTransactionStatus(TransactionInfo transactionInfo) {
        if( transactionInfo.getTransactionStatus().getState() ==   Status.trying){
            transactionInfo.getTransactionStatus().setState(Status.confirm);
            transactionRepository.save(transactionInfo);// TODO 加强健壮性
        }else {
            log.error("==> transactionInfo status error  {}",transactionInfo);
        }
    }
    public void cancelTransactionStatus(TransactionInfo transactionInfo) {
        if( transactionInfo.getTransactionStatus().getState() ==  Status.trying){
            transactionInfo.getTransactionStatus().setState(Status.cancel);
            transactionRepository.save(transactionInfo);
        }else {
            log.error("==> transactionInfo status error  {}",transactionInfo);

        }
    }


}

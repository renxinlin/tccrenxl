package com.tcc.renxl.interceptors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 分布式事务切面
 */
@Aspect
@Component
public class TransactionProxyFactoryBean implements Ordered {
    @Override
    public int getOrder() {
        // 需要考虑下，分布式事务的优先级和分库分表的优先级的影响
        // 目前我的推荐是分布式事务的优先级更高！
        // 所以分库分表的事务的优先级需要降低下
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Autowired
    private TransactionInteceptor transactionInteceptor;



    @Pointcut("@annotation(com.tcc.renxl.annocations.TransactionRen)")
    public void compensableService() {

    }

    @Around("compensableService()")
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        return  transactionInteceptor.invokeWithinTransaction(pjp);
    }


}

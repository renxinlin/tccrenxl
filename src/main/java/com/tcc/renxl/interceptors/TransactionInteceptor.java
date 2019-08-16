package com.tcc.renxl.interceptors;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * 持久化参数和事务状态
 * 参与者除了加入时候需要通知协调者，其他时候不通信，参与者异常，在提交回滚时候，协调者通知所有参与者
 *
 *
 * 分布式事务故障模型
 * 可保障算法在可预见故障下正常工作，在不可预见的灾难性故障则不能保障正常处理
 */
@Data
public class TransactionInteceptor extends TransactionInteceptorSupport implements InitializingBean {

    @Override
    protected void preTcc() {

    }


    @Override
    public void afterPropertiesSet()  {
        if(transactionManager == null){
            throw new RuntimeException("there is no transactionManager for rentcc");
        }

        if(recoverInterface == null){
            throw new RuntimeException("there is no recoverInterface for rentcc");
        }
    }


    // 本事务为平面事务
    // 分布式事务原理指出：嵌套事务具有更高的并发[作者不是非常理解]

    // 本事务为平面事务:将协调者参与者抽象化为应用进程本身


}

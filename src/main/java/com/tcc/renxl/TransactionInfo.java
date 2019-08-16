package com.tcc.renxl;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 由于是应用层事务:所以这里只关注transactionStatus
 * 这里 预留出隔离级别和传播特性
 * 但项目不做相关定义和实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true,fluent=false)
public class TransactionInfo implements Serializable {


    /**
     * 事务Id: 全局唯一
     */
    private String tramsactionId;


    /**
     * 并不使用，用于扩展事务传播特性和隔离级别
     */
    private TransactionAttribute transactionAttribute;


    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;


    /**
     * 业务的元数据
     */
    private BizMetadata metadata;


 ;




}

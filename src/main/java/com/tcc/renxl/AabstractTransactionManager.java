package com.tcc.renxl;


/**
 * 采用平面事务的方式处理
 * 基本需求:即是资源协调者也是参与者身份
 * 同时负责事务的处理
 */
public abstract class AabstractTransactionManager {
    /**
     * 预留隔离级别传播特性等
     * 这里不做实现
     * @param
     * @return
     * @throws TransactionException
     */
    public abstract TransactionInfo begin(TransactionInfo transactionInfo,BizMetadata bizMetadata) throws TransactionException;
    public abstract void commit(TransactionInfo transactionInfo) throws TransactionException;
    public abstract void rollback(TransactionInfo transactionInfo) throws TransactionException;

    public abstract void clear(TransactionInfo transactionInfo);

}

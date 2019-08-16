package com.tcc.renxl.repository;

import com.tcc.renxl.TransactionInfo;

/**
 * 根据分布式事务的指出:进程崩溃后要可以保障事务状态
 * 需要存储事务信息
 * 同时存储执行的源信息
 */
public interface TransactionRepository {
    long save(TransactionInfo transaction);

    long delete(TransactionInfo transaction);

    TransactionInfo select(Long transactionId);


}

package com.tcc.renxl.repository;

import com.tcc.renxl.RecoverTransactionInfo;
import com.tcc.renxl.TransactionInfo;

import java.util.List;

/**
 * 根据分布式事务的指出:进程崩溃后要可以保障事务状态
 * 需要存储事务信息
 * 同时存储执行的源信息
 */
public interface TransactionRecoverRepository {
    long save(RecoverTransactionInfo transaction);

    long delete(RecoverTransactionInfo transaction);

    TransactionInfo select(Long transactionId);

    /**
     * 通过锁获取，获取指定条数，如果状态是在处理中，则忽略这些，如果状态是不在处理中 则修改持久化的状态为在处理中；释放锁 进行处理
     * @param failureNum 获取指定的条数
     * @return
     */
    List<RecoverTransactionInfo> getRecoverTransactions(Long failureNum);


}

package com.tcc.renxl;

/**
 * 默认的传播特性等处理
 */
public interface DefaultTransactionAttributeProcess {
    TransactionInfo begin(TransactionInfo transactionInfo);
}

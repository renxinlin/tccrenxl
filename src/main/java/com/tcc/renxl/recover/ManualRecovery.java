package com.tcc.renxl.recover;

import com.tcc.renxl.RecoverTransactionInfo;

/**
 * 人工恢复
 */
public interface ManualRecovery {
    public void manualRecoveryForTime(RecoverTransactionInfo transactionInfo);
    public void manualRecoveryForNumber(RecoverTransactionInfo transactionInfo);
}

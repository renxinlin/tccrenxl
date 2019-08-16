package com.tcc.renxl.recover;

import com.tcc.renxl.RecoverTransactionInfo;

public class DefaultManualRecovery implements ManualRecovery {


    @Override
    public void manualRecoveryForTime(RecoverTransactionInfo transactionInfo) {
        // 可以决定是不是放弃处理;或者人工干预
    }

    @Override
    public void manualRecoveryForNumber(RecoverTransactionInfo transactionInfo) {
        // 可以决定是不是放弃处理;或者人工干预
    }
}

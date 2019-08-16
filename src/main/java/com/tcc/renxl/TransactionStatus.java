package com.tcc.renxl;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true,fluent=false)
public class TransactionStatus {
    /**
     * 事务ID
     */
    private String transactionId;
    /**
     * 事务状态
     */
    private Status state;
}

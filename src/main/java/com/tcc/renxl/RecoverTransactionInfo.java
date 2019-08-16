package com.tcc.renxl;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 由于是应用层事务:所以这里只关注transactionStatus
 * 这里 预留出隔离级别和传播特性
 * 但项目不做相关定义和实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true,fluent=false)
public class RecoverTransactionInfo implements Serializable {


    /**
     * 事务Id: 全局唯一
     */
    private String tramsactionId;


    /**
     * 事务状态
     */
    private TransactionStatus transactionStatus;


    /**
     * 业务的元数据
     */
    private BizMetadata metadata;






    // 第一次恢复的时间
    private Date firstStartRecoverTime ;
    // 失败的次数
    private int failureTimes = 0;



    private Date createTime = new Date() ;
    public void addFailureTimes(){
        failureTimes++;
    }


    public void initFirstStartRecoverTime(){
        firstStartRecoverTime = firstStartRecoverTime == null? new Date(): firstStartRecoverTime;
    }



}

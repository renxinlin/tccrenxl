package com.tcc.renxl;

/**
 * 事务的状态
 */
public enum Status {
    /**
     * 创建事务后属于此态
     */
    trying,

    /**
     * 取消事务
     */
    cancel,
    /**
     * 提交事务
     */
    confirm;

}

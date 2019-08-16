package com.tcc.renxl.util;

/**
 * 采用idworker
 * 组成接口
 * 标识符+ 41时间戳+ 10位机器标志+ +12位序列号
 *
 * 支持并发生成id在26万左右
 * */

public class IdUtil {
    private static IdWorkerHandler idWorkerHandler = new IdWorkerHandler();
    public String getNext(){

        return idWorkerHandler.nextId().toString();
    }
}

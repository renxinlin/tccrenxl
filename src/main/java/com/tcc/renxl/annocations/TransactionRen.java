package com.tcc.renxl.annocations;

import java.lang.annotation.*;

/**
 * 事务注解:业务大的切入点
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionRen {

    public String confirm() default "";

    public String cancel() default "";

    /**
     * 默认的初始恢复间隔,单位为s
     * @return
     */
    public int recoverIntegral() default 5;

    /**
     * 默认间隔每次调度失败增加的
     * @return
     */
    public int recoverIntegralAdd() default 0;


    /**
     * 失败N次之后的通知人工干预方式
     * @return
     */
    public long failureMsg() default Long.MAX_VALUE;









}

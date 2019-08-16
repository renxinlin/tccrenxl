package com.tcc.renxl.annocations;

import java.lang.annotation.*;

/**
 * 事务注解:业务大的切入点
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionRen {

     String confirm() default "";

     String cancel() default "";


}

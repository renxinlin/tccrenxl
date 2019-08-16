package com.tcc.renxl.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class BizMetadataUtil {
    public static Method getMethod(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
//        if (method.getAnnotation(TransactionRen.class) == null) {
//            try {
//                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
//            } catch (NoSuchMethodException e) {
//                return null;
//            }
//        }
        return method;
    }
}

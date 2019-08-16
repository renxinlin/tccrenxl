package com.tcc.renxl;

import com.tcc.renxl.annocations.TransactionRen;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 业务信息
 * 此处的属性里面存在transient 反序列化会丢失部分信息
 * 优化 降低序列化大小
 */
@Data
@NoArgsConstructor
public class BizMetadata implements Serializable {


    private Class target;
    private Class[] params;
    private Class<?> returnType;
    private  Object[] paramsValues;
    private String cancelMethodName;
    private String confirmmMethodName;

    public BizMetadata(Method method, Object[] paramsValues){
        this.paramsValues = paramsValues;
        target = method.getDeclaringClass();
        params = method.getParameterTypes();

        returnType = method.getReturnType();
        TransactionRen annotation = method.getAnnotation(TransactionRen.class);
        cancelMethodName = annotation.cancel();
        confirmmMethodName = annotation.confirm();
    }

}

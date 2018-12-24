package com.taoyuanx.littlerpc.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.taoyuanx.littlerpc.api.CallType;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {
    
    String version() default "";
    long timeout() default 3000;
    
    CallType callType() default CallType.ASYNC;
    
    String  route() default "";
    

}
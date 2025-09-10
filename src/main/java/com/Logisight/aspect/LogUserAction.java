package com.Logisight.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogUserAction {
    String actionType();
    String actionDetail() default "";
    boolean dynamicDetail() default false;
    Class<?> entityClass(); 
    CapturePhase phase() default CapturePhase.BOTH;
    String lookupField() default "id"; 
    boolean sendNotification() default false;
    String notificationMessage() default "";
    String notificationLink() default "";
}
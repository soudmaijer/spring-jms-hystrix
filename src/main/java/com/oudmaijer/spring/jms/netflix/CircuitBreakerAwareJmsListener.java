package com.oudmaijer.spring.jms.netflix;


import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CircuitBreakerAwareJmsListener {
    String [] hystrixCommandKeys() default "";

    double permitsPerSecond() default 0.5;
}

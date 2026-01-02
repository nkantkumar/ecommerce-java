package com.fix.ecommerce.jpa;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrackExecution {

    String value() default "";
    boolean enabled() default true;
}

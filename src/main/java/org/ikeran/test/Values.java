/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ikeran.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dhasenan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Values {
    double[] numbers() default {};
    String[] strings() default {};
    char[] chars() default {};
}

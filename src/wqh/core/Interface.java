package wqh.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2016/8/10.
 *
 * @author 王启航
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Interface {
    String value();
}

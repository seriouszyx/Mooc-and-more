package top.seriouszyx.diveinspringboot.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/11 20:15
 * @description：一级 {@link Repository @Repository}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
public @interface FirstLevelRepository {

    String value() default "";

}

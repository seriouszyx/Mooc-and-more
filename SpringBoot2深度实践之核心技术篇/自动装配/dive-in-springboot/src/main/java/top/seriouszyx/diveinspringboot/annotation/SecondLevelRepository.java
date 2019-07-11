package top.seriouszyx.diveinspringboot.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * @author ：Yixiang Zhao
 * @date ：Created in 2019/7/11 20:32
 * @description：二级 {@link Repository @Repository}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FirstLevelRepository
public @interface SecondLevelRepository {

    String value() default "";

}

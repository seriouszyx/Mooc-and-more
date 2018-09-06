package me.seriouszyx.reflect.test;


import org.junit.Test;

import java.lang.reflect.Method;

public class MethodTest {
    @Test
    /**
     *  测试公有方法
     * */
    public void demo1() throws Exception {
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        // 实例化
        Person p = (Person) clazz.newInstance();
        // 获得公有的方法
        Method method = clazz.getMethod("eat");
        // 执行该方法
        method.invoke(p);
    }

    @Test
    /**
     *  测试私有方法
     * */
    public void demo2() throws Exception{
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        // 实例化
        Person p = (Person) clazz.newInstance();
        Method method = clazz.getDeclaredMethod("run");
        method.setAccessible(true);
        method.invoke(p);
    }

    @Test
    /**
     *  测试私有方法带参数
     * */
    public void demo3() throws Exception {
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        // 实例化
        Person p = (Person) clazz.newInstance();
        Method method = clazz.getDeclaredMethod("sayHello", String.class);
        method.setAccessible(true);
        System.out.println(method.invoke(p, "张三"));
    }
}

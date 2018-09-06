package me.seriouszyx.reflect.test;

import org.junit.Test;

import java.lang.reflect.Field;

public class FieldTest {
    @Test
    /**
     *  测试共有的属性（需要将name设置为public）
     * */
    public void demo1() throws Exception {
        // 获得 class
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        // 获得属性
        Field field = clazz.getField("name");
        Person p = (Person) clazz.newInstance();
        // 操作属性
        field.set(p, "李四"); // p.name = "李四";
        Object obj = field.get(p);
        System.out.println(obj);
    }

    @Test
    /**
     *  测试私有的属性
     * */
    public void demo2() throws Exception {
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        Field field = clazz.getDeclaredField("sex");
        Person p = (Person) clazz.newInstance();
        // 私有属性，需要设置一个可访问的权限
        field.setAccessible(true);
        field.set(p, "男");
        System.out.println(field.get(p));
    }



}

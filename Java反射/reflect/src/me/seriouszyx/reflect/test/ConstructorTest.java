package me.seriouszyx.reflect.test;


import org.junit.Test;

import java.lang.reflect.Constructor;

public class ConstructorTest {
    @Test
    /**
     *  获得无参的构造方法
     * */
    public void demo1() throws Exception {
        // 获得类的字节码文件对应的对象
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        Constructor constructor = clazz.getConstructor();
        Person person = (Person) constructor.newInstance(); // 相当于 Person person = new Person();
        person.eat(); // eat...........
    }

    @Test
    /**
     *  获得有参数的构造方法
     * */
    public void demo2() throws Exception {
        Class clazz = Class.forName("me.seriouszyx.reflect.test.Person");
        Constructor constructor = clazz.getConstructor(String.class, String.class);
        Person person = (Person) constructor.newInstance("张三", "男");
        System.out.println(person); // Person{name='张三', sex='男'}
    }



}

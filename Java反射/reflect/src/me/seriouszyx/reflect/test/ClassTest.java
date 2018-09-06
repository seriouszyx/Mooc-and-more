package me.seriouszyx.reflect.test;

import org.junit.Test;

public class ClassTest {
    @Test
    /**
     * 获得Class 对象
     *  1、通过类名.class
     *  2、对象.class
     *  3、Class.forName()
     * */
    public void demo1() throws ClassNotFoundException {
        // 1、通过类名.class
        Class clazz1 = Person.class;

        // 2、对象.class
        Person person = new Person();
        Class clazz2 = person.getClass();

        // 3、Class.forName()
        Class clazz3 = Class.forName("me.seriouszyx.reflect.test.Person");

    }

}


#   Java 反射机制

##  反射的概述

Java 反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意方法和属性；这种`动态获取信息`以及`动态调用对象方法`的功能称为 Java 语言的反射机制。

Java 反射机制的作用：用来编写一些通用性较高的代码或者框架的时候使用。

##  Class 类

Java 中 `java.lang.Class` 类用于表示一个类的字节码（.class）文件。

获取 Class 对象的方法：

*   已知类和对象的情况下
    -   类名.class
    -   对象.getClass() —— Object 类提供
*   未知类和对象的情况下
    -   Class.forName("包名.类名")
    
下面是三种获取方法的代码实现：

```java
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
```

一般使用第三种方法。

##  Constructor 类

Constructor 类的实例对象代表类的一个构造方法。

*   得到某个类的所有构造方法
>   Constructor[] constructors = Class.forName("java.lang.String").getConstructors();

*   得到制定的构造方法并调用
>   Constructor constructor = Class.forName("java.lang.String").getConstructor(String.class); <br>
String str = (String)constructor.newInstance("abc);

*   Class 类的 newInstance() 方法用来调用类的默认构造方法
>   String obj = (String)Class.forName("java.lang.String).newInstance();

```java
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
```

##  Field 类

Field 类代表某个类中的一个成员变量，并提供动态的访问权限。

Field 对象的获得

*   得到所有的成员变量
    -   Field[] fields = c.getFields(); // 取得所有的 public 属性（包括父类继承）
    -   Field[] fields = c.getDeclaredFields(); // 取得所有声明的属性
*   得到指定的成员变量
    -   Field name = c.getField("name");
    -   Field name = c.getDeclaredField("name");
*   设置 Field 变量 是否可以访问
    -   field.setAccessible(boolean);
*   Field 变量值得读取、设置
    -   field.get(obj);
    -   field.set(obj, value);

```java
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
```

##  Method 类

Method 类代表某个类中的一个成员方法。

Method 对象的获得

*   获得所有方法
    -   getDeclaredMethods()
    -   getMethods()
*   获得指定的方法
    -   getDeclaredMethods(String name, Class<?>...parameterTypes)
    -   getMethods(String name, Class<?>...parameterTypes)
        
通过反射执行方法

            invoke(Object obj, Object...args)

```java
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
```


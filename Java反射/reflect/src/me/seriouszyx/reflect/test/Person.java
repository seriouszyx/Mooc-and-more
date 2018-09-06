package me.seriouszyx.reflect.test;


public class Person {

    private String name;
    private String sex;

    public Person() {
        super();
    }

    public Person(String name, String sex) {
        this.name = name;
        this.sex = sex;
    }

    public void eat() {
        System.out.println("eat...........");
    }

    private void run() {
        System.out.println("run...........");
    }

    private String sayHello(String name) {
        return "Hello " + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}

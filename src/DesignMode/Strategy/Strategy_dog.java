package DesignMode.Strategy;

/*
 * 策略模式
 * */
public class Strategy_dog {
    private String name;
    private int age;

    @Override
    public String toString() {
        return "Strategy_dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public Strategy_dog(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

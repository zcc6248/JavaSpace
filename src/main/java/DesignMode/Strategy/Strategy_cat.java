package DesignMode.Strategy;

/*
 * 策略模式
 * */
public class Strategy_cat {
    public String name;
    public int age;

    public Strategy_cat(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Strategy_cat{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

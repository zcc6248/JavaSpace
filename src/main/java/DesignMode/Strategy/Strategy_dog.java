package DesignMode.Strategy;

/*
 * 策略模式
 * */
public class Strategy_dog {
    public String name;
    public int weight;

    @Override
    public String toString() {
        return "Strategy_dog{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }

    public Strategy_dog(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }
}

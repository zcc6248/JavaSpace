package DesignMode.Strategy;

import java.util.Arrays;

/*
* 策略模式
* 根据不同方法实现不同逻辑，比如技能不同实现不同、特效、输出、武器伤害计算等
* */
public class Strategy {
    public static void main(String[] args) {
        Sorter<Strategy_cat> so = new Sorter<>();
        Strategy_cat[] arr = {new Strategy_cat("c1", 18), new Strategy_cat("c3", 1), new Strategy_cat("c2", 12)};
//        so.sorter(arr, new ComparatorAge());
        so.sorter(arr, (o1, o2)->{
            if (((Strategy_cat) o1).age < ((Strategy_cat) o2).age) return -1;
            else if(((Strategy_cat) o1).age > ((Strategy_cat) o2).age) return 1;
            return 0;
        });
        System.out.println(Arrays.toString(arr));
    }
}

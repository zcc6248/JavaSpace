package DesignMode.Strategy;

import java.util.Arrays;

/*
 * 策略模式
 * 根据不同方法实现不同逻辑，比如技能不同实现不同、技能特效、武器伤害计算等
 *
 * 需求
 * 1、实现一个排序函数，对传入的自定义类型进行排序，排序规则动态变化
 * 2、对于新增加的类，只要满足条件，也可以实现排序，并且排序函数不可再改变。
 * 例：
 * 猫类， 按照年龄从小到大排序
 * 狗类， 按照身高从大到小排序
 * 鸟类， 按照大小，体重从小到大排序
 * */

public class Strategy {
    public static void main(String[] args) {
        Sorter<Strategy_cat> so = new Sorter<>();
        Strategy_cat[] arr = {new Strategy_cat("c1", 18), new Strategy_cat("c3", 1), new Strategy_cat("c2", 12)};
//        so.sorter(arr, new ComparatorAge());            //每次都要new 可使用工厂模式将此类比较策略实现单例管理对象初始化
        so.sorter(arr, EStrategyFactory.getValue("ComparatorAge"));
        
/*        so.sorter(arr, (o1, o2)->{
            if (((Strategy_cat) o1).age < ((Strategy_cat) o2).age) return -1;
            else if(((Strategy_cat) o1).age > ((Strategy_cat) o2).age) return 1;
            return 0;
        });*/
        System.out.println(Arrays.toString(arr));
    }
}

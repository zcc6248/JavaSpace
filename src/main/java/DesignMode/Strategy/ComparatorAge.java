package DesignMode.Strategy;

public class ComparatorAge implements Comparator<Strategy_cat>{
    @Override
    public int compare(Strategy_cat o1, Strategy_cat o2) {
        return Integer.compare(o1.age, o2.age);
    }
}

package DesignMode.Strategy;

public class ComparatorAge implements Comparator<Strategy_cat>{
    @Override
    public int compare(Strategy_cat o1, Strategy_cat o2) {
        if (o1.age < o2.age) return -1;
        if (o1.age > o2.age) return 1;
        return 0;
    }
}

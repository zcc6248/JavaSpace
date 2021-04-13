package DesignMode.Strategy;

import java.util.Comparator;

public class ComparatorAge implements Comparator{
    @Override
    public int compare(Object o1, Object o2) {
        if (((Strategy_cat) o1).age < ((Strategy_cat) o2).age) return -1;
        if (((Strategy_cat) o1).age > ((Strategy_cat) o2).age) return 1;
        return 0;
    }
}

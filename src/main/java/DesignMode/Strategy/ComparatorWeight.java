package DesignMode.Strategy;

public class ComparatorWeight implements Comparator<Strategy_dog>{
    @Override
    public int compare(Strategy_dog o1, Strategy_dog o2) {
        return Integer.compare(o1.weight, o2.weight);
    }
}

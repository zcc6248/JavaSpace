package DesignMode.Strategy;

public enum EStrategyFactory {
    ComparratorAge("ComparatorAge", new ComparatorAge());





    private String key;
    private Comparator value;

    EStrategyFactory(String age, Comparator comparator) {
        this.key = age;
        this.value = comparator;
    }

    public static Comparator getValue(String str){
        EStrategyFactory[] es = values();
        for(EStrategyFactory item : es){
            if (item.key.equals(str)){
                return item.value;
            }
        }
        return null;
    }
}

package DesignMode.Strategy;

public enum EStrategyFactory {
    ComparratorAge("ComparatorAge", new ComparatorAge()),
    ComparratorWeight("ComparatorWeight", new ComparatorWeight());



    private String key;
    private Comparator value;

    EStrategyFactory(String key, Comparator value) {
        this.key = key;
        this.value = value;
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

package DesignMode.Strategy;

public enum EStrategyFactory {
    ComparratorAge("ComparatorAge", new ComparatorAge()),
    ComparratorWeight("ComparatorWeight", new ComparatorWeight());



    private String key;
    private IComparator value;

    EStrategyFactory(String key, IComparator value) {
        this.key = key;
        this.value = value;
    }

    public static IComparator getValue(String str){
        EStrategyFactory[] es = values();
        for(EStrategyFactory item : es){
            if (item.key.equals(str)){
                return item.value;
            }
        }
        return null;
    }
}

package DesignMode.Singleton;


/*
* 枚举单例，可防止反序列
* */
public enum ESingleton02 {

    instance;

    public void hello(){
        System.out.println(instance.hashCode());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                ESingleton02.instance.hello();
            }).start();
        }
    }
}

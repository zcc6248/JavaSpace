package DesignMode.Singleton;


/*
* 枚举单例，可防止反序列
* */
public enum Singleton02 {

    instance;

    public void hello(){
        System.out.println(instance.hashCode());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                Singleton02.instance.hello();
            }).start();
        }
    }
}

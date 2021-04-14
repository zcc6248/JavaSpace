package DesignMode.Singleton;

/*
* 单例模式
* */
public class Singleton {
    private static final Singleton instance = new Singleton();

    public static Singleton getInstance() {
        return instance;
    }
    public void Hello(){
        System.out.println(this.hashCode());
    }

    private Singleton() {
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                System.out.println(Singleton.getInstance().hashCode());
            }).start();
        }
    }
}

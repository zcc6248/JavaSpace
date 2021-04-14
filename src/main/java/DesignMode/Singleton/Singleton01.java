package DesignMode.Singleton;

/*
* DCL双重锁
* */
public class Singleton01 {
    private static volatile Singleton01 instance;
    private Singleton01() {
    }

    public static Singleton01 getInstance() {
        if (instance == null) {
            synchronized (Singleton01.class) {
                if (instance == null) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    instance = new Singleton01();
                }
            }
        }
        return instance;
    }
    public void Hello(){
        System.out.println(this.hashCode());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                Singleton01 s = Singleton01.getInstance();
                s.Hello();
            }).start();
        }
    }
}

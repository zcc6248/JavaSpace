package Proxy;

import DesignMode.Factory.FactoryMethod.Bird;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class staticProxy {
    public static void main(String[] args) {
        brid brid = new brid();
        brid.fly();
        flyProxy flyProxy = new flyProxy(new brid());
        flyProxy.fly();
        flyProxy.eat();
    }
}

class flyProxy implements Ifly{
    private Ifly ifly = null;

    public flyProxy(Ifly ifly) {
        this.ifly = ifly;
    }

    @Override
    public void fly() {
        System.out.println("飞行开始");
        ifly.fly();
        System.out.println("飞行结束");
    }

    @Override
    public void eat() {
        System.out.println("飞行开始");
        ifly.eat();
        System.out.println("飞行结束");
    }
}

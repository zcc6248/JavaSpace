package DesignMode.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class dyncProxy {
    public static void main(String[] args) {
        brid brid = new brid();
        brid.fly();
//        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        Ifly p = (Ifly) Proxy.newProxyInstance(brid.getClass().getClassLoader(), brid.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("飞行开始");
                Object o = method.invoke(brid, args);
                System.out.println("飞行结束");
                return o;
            }
        });
        p.fly();
        p.eat();
    }
}

interface Ifly{
    void fly();
    void eat();
}

class brid implements Ifly{

    @Override
    public void fly() {
        System.out.println("fei.............");
    }

    @Override
    public void eat() {
        System.out.println("chi..............");
    }
}
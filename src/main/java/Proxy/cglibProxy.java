package Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class cglibProxy {
    public static void main(String[] args) {
        brid brid = new brid();
        brid.fly();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Proxy.brid.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("飞行开始");
//                method.invoke(o, objects);
                methodProxy.invokeSuper(o, objects);
                System.out.println("飞行结束");
                return null;
            }
        });
        brid o = (brid) enhancer.create();
        o.fly();
        o.eat();
    }
}

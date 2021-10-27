package RPCDemo.proxy;

import RPCDemo.rpc.protocol.MessageBody;
import RPCDemo.rpc.protocol.MessageHead;
import RPCDemo.rpc.Dispatcher;
import RPCDemo.rpc.transport.ClientChannelFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class ProxyFactory {

    private static Dispatcher dispatcher = Dispatcher.instance;

    public static <T>T proxyGet(Class<T> inter) {
        return (T) Proxy.newProxyInstance(inter.getClassLoader(), new Class<?>[]{inter}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object res = null;
                //TODO 区分是远程调用还是本地调用
                Object mapping = dispatcher.getMapping(inter.getName());
                if (mapping != null) {
                    MessageBody cont = new MessageBody(inter.getName(), method.getName(), method.getParameterTypes(), args);

                    CompletableFuture<Object> transport = ClientChannelFactory.instance.transport(cont);
                    res = transport.get();
                }else{
                    System.out.println("local ...............");
                    Class<?> aClass = mapping.getClass();
                    Method meth = aClass.getMethod(method.getName(), method.getParameterTypes());
                    res = meth.invoke(mapping, args);
                }
                return res;
            }

        });
    }
}

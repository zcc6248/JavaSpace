package RPCDemo.rpc.transport;


import RPCDemo.rpc.UnpackMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestCallback {
    private static AtomicInteger repeatNum = new AtomicInteger(0);
    private static ConcurrentHashMap<Long, CompletableFuture> map = new ConcurrentHashMap<Long, CompletableFuture>();

    public static void addCallback(Long requestid, CompletableFuture r){
        map.putIfAbsent(requestid, r);
    }
    public static void runCallback(UnpackMessage mess){
        CompletableFuture runnable = map.get(mess.getHead().getRequestId());
        if(runnable != null){
            runnable.complete(mess.getBody().getResult());
            map.remove(mess.getHead().getRequestId());
        }else {
            repeatNum.incrementAndGet();
        }
    }
    public static int getRepeatNum(){
        return repeatNum.get();
    }
}

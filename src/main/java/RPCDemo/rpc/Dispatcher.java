package RPCDemo.rpc;

import java.util.concurrent.ConcurrentHashMap;

public enum  Dispatcher {

    instance;

    private static ConcurrentHashMap<String, Object> mappings = new ConcurrentHashMap<>();

    public void addMapping(String key, Object obj){
        mappings.putIfAbsent(key, obj);
    }

    public Object getMapping(String key){
        return mappings.get(key);
    }
}

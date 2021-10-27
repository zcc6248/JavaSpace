package RPCDemo.service;

public class MyCar implements Icar {
    @Override
    public String hello(String string) {
        return "ser return: " + string;
    }
}

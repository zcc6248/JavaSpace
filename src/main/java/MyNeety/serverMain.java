package MyNeety;

public class serverMain {
    public static void main(String[] args) {
        nioGroup nioGroup = new nioGroup(1);
        nioGroup.bind(9090);
        nioGroup.bind(9080);
    }
}

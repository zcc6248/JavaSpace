package DesignMode.Composite;

public class Text implements Node{
    String name;

    public Text(String name) {
        this.name = name;
    }

    @Override
    public void print() {
        System.out.println(name);
    }
}

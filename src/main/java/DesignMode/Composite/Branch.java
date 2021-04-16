package DesignMode.Composite;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

public class Branch implements Node{
    String name;
    List<Node> node = new ArrayList<>();

    public Branch(String name) {
        this.name = name;
    }

    public void addNode(Node node){
        this.node.add(node);
    }

    @Override
    public void print() {
        System.out.println(name);
    }
}

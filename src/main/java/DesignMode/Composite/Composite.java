package DesignMode.Composite;

/*
* 组合模式
* 树状结构使用
* */
public class Composite {
    public static void main(String[] args) {
        Branch node = new Branch("main");
        Branch node11 = new Branch("目录11");
        Branch node12 = new Branch("目录12");
        Branch node21 = new Branch("目录21");
        Branch node22 = new Branch("目录22");
        Text text11 = new Text("text11");
        Text text22 = new Text("text22");

        node.addNode(node11);
        node.addNode(node12);

        node11.addNode(text11);

        node12.addNode(node21);
        node12.addNode(node22);

        node22.addNode(text22);

        print(node, 1);
    }
    public static void print(Node node, int i){
        for (int j = 0; j < i; j++) {
            System.out.print("----");
        }
        node.print();
        if(node instanceof Branch){
            for(Node item: ((Branch) node).node){
                print(item, i + 1);
            }
        }
    }
}

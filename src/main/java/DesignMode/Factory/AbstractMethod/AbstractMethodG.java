package DesignMode.Factory.AbstractMethod;

/*
* 工厂方法模式
* 现在工厂可以建造小鸟和乌鸦，如果要建造老鹰，只需要新建老鹰对象继续IFly接口即可
* 但如果现在需要一次创建小鸟和乌鸦， 一次创建乌鸦和老鹰，就需要抽象工厂模式
* 简单理解抽象工厂模式适用于一次创建多个对象，即一对象族。产品族可扩展。
* 工厂方法模式适用一次创建一个对象。对象种类可扩展。
* */

public class AbstractMethodG extends AbstractFactory {


    @Override
    public IFly createBird() {
        return new BirdG();
    }

    @Override
    public IFly createBCrow() {
        return new CrowG();
    }

    @Override
    public IFly createGlede() {
        return new GledeG();
    }

    public static void main(String[] args) {
        //实例化生产雄性的工厂
        AbstractMethodB boy = new AbstractMethodB();
        //实例化生产雌性的工厂
        AbstractMethodG gril = new AbstractMethodG();

        IFly birdb = boy.createBird();
        IFly birdg = gril.createBird();

        birdb.fly();
        birdb.talk();
        birdb.sex();

        birdg.fly();
        birdg.talk();
        birdg.sex();
    }
}

package DesignMode.Factory.FactoryMethod;

public class Bird implements IFly{
    @Override
    public void talk() {
        System.out.println("bird is talk");
    }

    @Override
    public void fly() {
        System.out.println("bird is fly");
    }
}

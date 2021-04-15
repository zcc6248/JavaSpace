package DesignMode.Factory.FactoryMethod;

public class Crow implements IFly{
    @Override
    public void talk() {
        System.out.println("crow is talk");
    }

    @Override
    public void fly() {
        System.out.println("Crow is fly");
    }
}

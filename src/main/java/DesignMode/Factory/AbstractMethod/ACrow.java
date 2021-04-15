package DesignMode.Factory.AbstractMethod;

public abstract class ACrow implements IFly {
    @Override
    public void talk() {
        System.out.println("crow is talk");
    }

    @Override
    public void fly() {
        System.out.println("Crow is fly");
    }
}

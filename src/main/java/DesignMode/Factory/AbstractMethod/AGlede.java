package DesignMode.Factory.AbstractMethod;

public abstract class AGlede implements IFly{
    @Override
    public void talk() {
        System.out.println("glede is talk");
    }

    @Override
    public void fly() {
        System.out.println("glede is fly");
    }
}

package DesignMode.Factory.AbstractMethod;

public abstract class ABird implements IFly {
    @Override
    public void talk() {
        System.out.println("bird is talk");
    }

    @Override
    public void fly() {
        System.out.println("bird is fly");
    }
}

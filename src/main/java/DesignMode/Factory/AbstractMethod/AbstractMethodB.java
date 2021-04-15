package DesignMode.Factory.AbstractMethod;

public class AbstractMethodB extends AbstractFactory
{
    @Override
    public IFly createBird() {
        return new BirdB();
    }

    @Override
    public IFly createBCrow() {
        return new CrowB();
    }

    @Override
    public IFly createGlede() {
        return new GledeB();
    }
}

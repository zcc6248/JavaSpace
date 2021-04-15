package DesignMode.Factory.AbstractMethod;

import DesignMode.Factory.FactoryMethod.Bird;
import DesignMode.Factory.FactoryMethod.Crow;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractFactory {
    public abstract IFly createBird();
    public abstract IFly createBCrow();
    public abstract IFly createGlede();
}

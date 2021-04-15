package DesignMode.Factory.FactoryMethod;

/*
* 工厂方法模式
* 现在工厂可以建造小鸟和乌鸦，如果要建造老鹰，只需要新建老鹰对象继续IFly接口即可
* 但如果现在需要一次创建小鸟和乌鸦， 一次创建乌鸦和老鹰，就需要抽象工厂模式
* 简单理解抽象工厂模式适用于一次创建多个对象，即一对象族。产品族可扩展。
* 工厂方法模式适用一次创建一个对象。对象种类可扩展。
* */
public class FactoryMethod {
    public static <T extends IFly> T Create(Class<T> c)
    {
        try {
            return (T)Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        };
        return null;
    }


    public static void main(String[] args) {
        IFly bird = FactoryMethod.Create(Bird.class);
        bird.talk();
        bird.fly();

        IFly crow = FactoryMethod.Create(Crow.class);
        crow.talk();
        crow.fly();
    }
}

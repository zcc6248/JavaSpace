package DesignMode.Decorator.v1;


/*
* 装饰模式
* 实现不同武器，有自己独特攻击方式
* */
abstract class IWeapon{
    abstract void attack();
}

class Weapon1 extends IWeapon{
    @Override
    void attack() {
        System.out.println("跳砍");
    }
}

class Weapon2 extends IWeapon{
    @Override
    void attack() {
        System.out.println("双持");
    }
}

public class Decorator {
    public static void main(String[] args) {
        IWeapon w1 = new Weapon1();
        IWeapon w2 = new Weapon2();
        w1.attack();
        w2.attack();
    }
}

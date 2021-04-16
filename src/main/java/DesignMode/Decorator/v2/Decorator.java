package DesignMode.Decorator.v2;

/*
 * 装饰模式
 * 实现不同武器，有自己独特攻击方式
 *
 * 现在需要攻击时增加特效......
 * 使用继承会使类膨胀，每多一个需求都需要增加继承类
 * */
abstract class IWeapon{
    abstract void attack();
}

class Weapon1 extends IWeapon {
    @Override
    void attack() {
        System.out.println("跳砍");
    }
}

class Weapon2 extends IWeapon {
    @Override
    void attack() {
        System.out.println("双持");
    }
}

abstract class Decorator extends IWeapon{
    private final IWeapon iWeapon;

    public Decorator(IWeapon iWeapon) {
        this.iWeapon = iWeapon;
    }

    public void attack(){
        this.iWeapon.attack();
    }
}

class EffectDecorator extends Decorator{
    public EffectDecorator(IWeapon iWeapon) {
        super(iWeapon);
    }

    public void effect(){
        System.out.println("激光");
    }

    @Override
    public void attack() {
        effect();
        super.attack();
    }
}

class Effect1Decorator extends Decorator{
    public Effect1Decorator(IWeapon iWeapon) {
        super(iWeapon);
    }

    public void effect(){
        System.out.println("雷电");
    }

    @Override
    public void attack() {
        effect();
        super.attack();
    }
}


class Decoratorv2 {
    public static void main(String[] args) {
        IWeapon w1 = new Weapon1();
        IWeapon w2 = new Weapon2();
        w2 = new EffectDecorator(w2);
        w2 = new Effect1Decorator(w2);
        w1.attack();
        System.out.println("===================");
        w2.attack();
    }
}

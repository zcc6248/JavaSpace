package DesignMode.Observer.v2;

import java.util.*;

/*
 * 观察者模式
 * 简单成就系统
 * 有三个成就
 *   1、登录天数10天，获得神盾
 *   2、充值100元，获得神甲
 *   3、登录50天，充值50元，获得刑天斧
 *
 *
 *
 * 新添加需求
 *   1、首充6元，获得神盔
 *   2、累计杀敌100， 获得神骑
 * */


//定义计数器枚举
enum EAchievementType{
    logday, Paynum, Kill, FirstPay
}

//计数器
class Counter{
    //成就类型
    EAchievementType type;
    //当前完成度
    int number;

    public Counter(EAchievementType ea, int num){
        type = ea;
        number = num;
    }
}

//定义成就抽象方法
abstract class Achievement{
    //计数器与对应的目标数
    public HashMap<Counter, Integer> counters = new HashMap<>();
    //奖励
    public abstract void award();
}

class dayAchi extends Achievement{
    public dayAchi() {
        counters.put(new Counter(EAchievementType.logday, 0), 10);
    }

    @Override
    public void award() {
        System.out.println("登录天数10天,   获得神盾");
    }
}

class PayAchi extends Achievement{
    public PayAchi() {
        counters.put(new Counter(EAchievementType.Paynum, 0), 100);
    }

    @Override
    public void award() {
        System.out.println("充值100元,   获得神甲");
    }
}

//新加对象
class OnePay extends Achievement{
    public OnePay() {
        counters.put(new Counter(EAchievementType.FirstPay, 0), 6);
    }

    @Override
    public void award() {
        System.out.println("首充6元,  获得神盔");
    }
}

class Kill extends Achievement{
    public Kill() {
        counters.put(new Counter(EAchievementType.Kill, 0), 100);
    }

    @Override
    public void award() {
        System.out.println("累计杀敌100,  获得神骑");
    }
}
///

class LogAndPayAchi extends Achievement{
    public LogAndPayAchi() {
        counters.put(new Counter(EAchievementType.Paynum, 0), 50);
        counters.put(new Counter(EAchievementType.logday, 0), 5);
    }

    @Override
    public void award() {
        System.out.println("登录50天，充值50元,   获得刑天斧");
    }
}

//成就管理类
class AchievementManage{
    List<Achievement> observer = new ArrayList<>();

    public AchievementManage addAchi(Achievement ea){
        observer.add(ea);
        return this;
    }

    public void incr(EAchievementType ea, int i){
        Iterator<Achievement> iter = observer.iterator();
        if(!iter.hasNext()){
            System.out.println("无成就");
            return;
        }
        while (iter.hasNext()){
            Achievement item = iter.next();
            for(Map.Entry<Counter, Integer> achi: item.counters.entrySet()){
                Counter c = achi.getKey();
                if(c.type.equals(ea)){
                    if((c.number += i) >= achi.getValue()){
                        //遍历此成就集合，看是否全部完成
                        boolean isend = true;
                        for(Map.Entry<Counter, Integer> param: item.counters.entrySet()) {
                            Counter ci = param.getKey();
                            if(ci.number < param.getValue()){
                                isend = false;
                                break;
                            }
                        }
                        //全部完成，则移除观察者，并执行奖励
                        if(isend){
                            item.award();
                            iter.remove();
                        }
                    }
                }
            }
        }
    }
}

class Observer02{
    public static void main(String[] args) {
        //实例化三个观察对象
        Achievement day = new dayAchi();
        Achievement pad = new PayAchi();
        Achievement dayandpad = new LogAndPayAchi();
        Achievement onepad = new OnePay();
        Achievement kill = new Kill();
        //实例化管理器
        AchievementManage ma = new AchievementManage();
        //添加观察者
        ma.addAchi(day).addAchi(pad).addAchi(dayandpad).addAchi(onepad).addAchi(kill);


        //、
        System.out.println("============累计登录1天=============");
        ma.incr(EAchievementType.logday, 1);
        System.out.println("============累计登录10天=============");
        ma.incr(EAchievementType.logday, 10);
        System.out.println("============累计登录49天============");
        ma.incr(EAchievementType.logday, 39);
        System.out.println("============首充值10元============");
        ma.incr(EAchievementType.Paynum, 10);
        ma.incr(EAchievementType.FirstPay, 10);
        System.out.println("============累计登录50天。累计充值90元============");
        ma.incr(EAchievementType.logday, 1);
        ma.incr(EAchievementType.Paynum, 80);
        System.out.println("=============累计充值99元===========");
        ma.incr(EAchievementType.Paynum, 9);
        System.out.println("=============累计充值100元===========");
        ma.incr(EAchievementType.Paynum, 1);
        System.out.println("=============累计登录1000天,累计充值1000元===========");
        ma.incr(EAchievementType.logday, 950);
        ma.incr(EAchievementType.Paynum, 900);

        System.out.println("=============累计杀敌50===========");
        ma.incr(EAchievementType.Kill, 50);
        System.out.println("=============累计杀敌100===========");
        ma.incr(EAchievementType.Kill, 50);

        System.out.println("=============累计登录2000天,累计充值2000元===========");
        ma.incr(EAchievementType.logday, 1950);
        ma.incr(EAchievementType.Paynum, 1900);

    }
}

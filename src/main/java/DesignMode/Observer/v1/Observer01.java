package DesignMode.Observer.v1;

import java.util.*;

/*
* 观察者模式
* 简单成就系统
* 有三个成就
*   1、登录天数10天，获得神盾
*   2、充值100元，获得神甲
*   3、登录50天，充值50元，获得刑天斧
*
* */

//定义计数器枚举
enum EAchievementType{
    logday, Paynum
}

//计数器
class Counter{
    //成就id
    EAchievementType type;
    //当前完成度
    int number;

    public Counter(EAchievementType ea, int num){
        type = ea;
        number = num;
    }
}

abstract class Achievement{
    //计数器与对应的目标数
    public HashMap<Counter, Integer> counters = new HashMap<>();

    public abstract void award();
}

class dayAchi extends Achievement{
    public dayAchi() {
        counters.put(new Counter(EAchievementType.logday, 0), 10);
    }

    @Override
    public void award() {
        System.out.println("获得神盾");
    }
}

class PayAchi extends Achievement{
    public PayAchi() {
        counters.put(new Counter(EAchievementType.Paynum, 0), 100);
    }

    @Override
    public void award() {
        System.out.println("获得神甲");
    }
}

class LogAndPay extends Achievement{
    public LogAndPay() {
        counters.put(new Counter(EAchievementType.Paynum, 0), 50);
        counters.put(new Counter(EAchievementType.logday, 0), 5);
    }

    @Override
    public void award() {
        System.out.println("获得刑天斧");
    }
}

//成就管理类
class AchievementManage{
    List<Achievement> a = new ArrayList<>();

    public AchievementManage addAchi(Achievement ea){
        a.add(ea);
        return this;
    }

    public void incr(EAchievementType ea, int i){
        Iterator<Achievement> iter = a.iterator();
        if(!iter.hasNext()){
            System.out.println("无成就");
        }
        while (iter.hasNext()){
            Achievement item = iter.next();
            for(Map.Entry<Counter, Integer> ite: item.counters.entrySet()){
                Counter c = ite.getKey();
                if(c.type.equals(ea)){
                    if((c.number += i) >= ite.getValue()){
                        //遍历此成就集合，看是否全部完成
                        boolean isend = true;
                        for(Map.Entry<Counter, Integer> pa: item.counters.entrySet()) {
                            Counter ci = pa.getKey();
                            if(ci.number < pa.getValue()){
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

class Observer01{
    public static void main(String[] args) {
        //实例化三个观察对象
        Achievement day = new dayAchi();
        Achievement pad = new PayAchi();
        Achievement dayandpad = new LogAndPay();
        //实例化管理器
        AchievementManage ma = new AchievementManage();
        //添加观察者
        ma.addAchi(day).addAchi(pad).addAchi(dayandpad);


        //、
        System.out.println("============累计登录1天=============");
        ma.incr(EAchievementType.logday, 1);
        System.out.println("============累计登录10天=============");
        ma.incr(EAchievementType.logday, 10);
        System.out.println("============累计登录49天============");
        ma.incr(EAchievementType.logday, 39);
        System.out.println("============累计登录50天。累计充值90元============");
        ma.incr(EAchievementType.logday, 1);
        ma.incr(EAchievementType.Paynum, 90);
        System.out.println("=============累计充值99元===========");
        ma.incr(EAchievementType.Paynum, 9);
        System.out.println("=============累计充值100元===========");
        ma.incr(EAchievementType.Paynum, 1);
        System.out.println("=============累计登录1000天,累计充值1000元===========");
        ma.incr(EAchievementType.logday, 950);
        ma.incr(EAchievementType.Paynum, 900);

    }
}

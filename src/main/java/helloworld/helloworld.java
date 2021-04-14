package helloworld;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class helloworld {

	public static void main(String[] args) throws CloneNotSupportedException {
		Run a = new Cat("汤姆");
		Run b = new Dog("戴维");
		a.run();
		b.run();
		if(a instanceof Cat)
		{
			Cat ca = (Cat)((Cat) a).clone();
			System.out.println(ca.name + " Cat");
		}
		if(a instanceof Dog)
		{
			Cat ca = (Cat)((Cat) a).clone();
			System.out.println(ca.name + " Dog");
		}
		System.out.println("========================");
		Calendar calendar = Calendar.getInstance();
		Date aa = new Date();
		System.out.println(String.format("一周中的第%s天，第一天为周末" , calendar.get(calendar.DAY_OF_WEEK)));
		SimpleDateFormat d = new SimpleDateFormat("y-M-d hh:mm:ss");
		System.out.println(d.format(aa));
	}
	
}
 abstract class Run implements Cloneable{
	public abstract void run();
}

 class Cat extends Run {
	public String name;
	
	public Cat(String _name)
	{
		this.name = _name;
	}
	public Object clone () throws CloneNotSupportedException{
		return super.clone();
	}
	public void run()
	{
		System.out.println("猫跑了 "+ this.name);
	}
}

 class Dog extends Run {
	public String name;
	
	public Dog(String _name)
	{
		this.name = _name;
	}
	public void run()
	{
		System.out.println("狗跑了 "+ this.name);
	}
}


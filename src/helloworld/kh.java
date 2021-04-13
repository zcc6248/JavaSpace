package helloworld;

public class kh {

	/*
	 * 给一随机函数f,等概率返回1-5中的一个数字，这是你唯一可使用的随机机制，
	 *如何实现等概率返回1-7中的数字
	*/
	
	//等概率返回1-5
	public static int f()
	{
		return (int)(Math.random() * 5) + 1;
	}
	
	//等概率返回0/1
	public static int a()
	{
		int i = 0;
		do {
			i = f();
		}while(i == 3);
		return i < 3 ? 0 : 1;
	}
	
	//等概率返回1-7也就是0-6
	public static int b() {
		int i = 0;
		do {
			i = (a() << 2) + (a() << 1) + a();
		}while(i == 7);
		return i;
	}
	
	public static void main(String[] args){
		int[] count = new int[8];
		int j = 0; 
		for (int i =0; i<4000000; i++) {
			count[b()] ++ ;
		}
		for (int i=0;i<count.length;i++) {
			System.out.println(i + ": " + count[i]);
		}
	}

}

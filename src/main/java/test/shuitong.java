package test;

public class shuitong {

	/*
	 * 给定一个数组，想象成一个桶。问最多能装多少水
	 * 例【1，5，3，6】   最多装2格水
	 *          O
	 *    O  ~  O
	 *    O  ~  O
	 *    O  O  O 
	 *    O  O  O
	 * O  O  O  O
	 * 
	 */
	
	public static int shui2(int[] param) {
		if(param == null || param.length < 2) return 0;
		
		int[] L = new int[param.length];
		int[] R = new int[param.length];
	
		for(int i=1;i<param.length;i++) {
			L[i] = Math.max(param[i], param[i - 1]);
		}
		for(int i=param.length - 2;i>0;i--) {
			R[i] = Math.max(param[i], param[i + 1]);
		}
		int shui = 0;
		for(int i = 1; i<param.length-1;i++)
		{
			shui += Math.max(0, Math.min(L[i], R[i]) - param[i]);
		}
		return shui;
	}
	
	public static int shui(int[] param) {
		if(param == null || param.length < 2) return 0;
		
		int L = 1, R = param.length-2, shui = 0;
		int lmax = param[0], rmax = param[R + 1];
		while(L<=R) {
			if(lmax <= rmax) {
				shui += Math.max(0, lmax - param[L]);
				lmax = Math.max(lmax, param[L++]);
			}else {
				shui += Math.max(0, rmax - param[R]);
				rmax = Math.max(rmax, param[R--]);
			}
		}
		return shui;
	}
	
	public static void main(String[] args) {
		int j = 400000;
		while(j > 0) {
			int[] a = new int[500];
			for(int i =0;i<500;i++) {
				a[i] = (int)Math.random() * 500;
			}
			if(shui(a) != shui2(a)) System.out.println("false");
			j --;
		}
		System.out.println("success");
	}
}

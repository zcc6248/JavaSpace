package gun;

public class gun {
	public static void main(String[] args) {
		Soldier sold = new Soldier();
		sold.setgun(new Handgun());
		sold.shoot();
	}
}

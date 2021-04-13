package gun;

public class Soldier {
	private Igun igun;
	
	public void setgun(Igun igun) {
		this.igun = igun;
	}
	
	public void shoot() {
		if(igun != null)
			igun.shoot();
	}
}

package test;

enum CarTypeEnum {

    CAR_TYPE_BMW("bmw", "宝马"),
    CAR_TYPE_BC("bc", "奔驰"),
    CAR_TYPE_AUDI("audi", "奥迪");

    private String type;
    private String desc;

    private CarTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getValue(String type) {
        CarTypeEnum[] carTypeEnums = values();
        for (CarTypeEnum carTypeEnum : carTypeEnums) {
        	System.out.println(carTypeEnum.toString() + " " + carTypeEnum.desc());
            if (carTypeEnum.type().equals(type)) {
                return carTypeEnum.desc();
            }
        }
        return null;
    }

    public static String getType(String desc) {
        CarTypeEnum[] carTypeEnums = values();
        for (CarTypeEnum carTypeEnum : carTypeEnums) {
           	System.out.println(carTypeEnum.toString() + " " + carTypeEnum.type());
            if (carTypeEnum.desc().equals(desc)) {
                return carTypeEnum.type();
            }
        }
        return null;
    }
    										
    private String type() {
        return this.type;
    }
								  
    private String desc() {
        return this.desc;
    }
}
public class enum01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(CarTypeEnum.getValue("bc"));
		System.out.print(CarTypeEnum.getType("奔驰"));
	}

}

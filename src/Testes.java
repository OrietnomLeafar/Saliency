
public class Testes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CIELab c = new CIELab();
		
		float[] rgb = {60,60,60};
		
		float[] Lab = c.fromRGB(rgb);
		System.out.println(Lab[0]+"  "+Lab[1]+"  "+Lab[2]);
	}

}

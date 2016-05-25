
public class Conversion {
	public static String decimalToBinary(int a) {
		String b = "";
		while (a > 0) {
			b = ((a % 2 == 1) ? 1 : 0) + b;
			a /= 2;
		}
		return b;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++)
			System.out.println(decimalToBinary(i));
	}
}

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import java.util.Scanner;

/**
 * Created by Steve on 2/4/2015.
 */
public class GPIODriver {

	public static void main(String[] args) throws InterruptedException {
		GpioController gpio = GpioFactory.getInstance();
		System.out.println(gpio.toString());
		try{
			int[] pins = {0,1,2,3,4,5,6,7};
			LedDigit ld = new LedDigitSerial(gpio);
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.print("Enter a number: ");
				int n = sc.nextInt();
				if (n<0){
					System.out.println("Done!");
					ld.clear();
					gpio.shutdown();
					break;
				}
				else if(n==10){
					System.out.print("Enter number of spins: ");
					int m = sc.nextInt();
					ld.spin(m);
				}
				else if(n==11){
					System.out.println("Enter number of blinks: ");
					int m = sc.nextInt();
					ld.blink(m);
				}
				else if(n==12){
					System.out.print("Enter string: ");
					String s = sc.next();
					ld.write(s);
				}
				else if(n==13){
					System.out.println("Count Down!");
					ld.blink(5, 200);
					for (int i = 9; i >= 0 ; i--) {
						ld.set(i);
						Thread.sleep(500);
						ld.clear();
						Thread.sleep(10);
					}
					ld.set('.');
					Thread.sleep(100);
					ld.clear();
					ld.blink(5);
					ld.spin(2);
				}

				else {
					ld.set(n);
				}
			}
		} catch (Exception e){
			gpio.shutdown();
			e.printStackTrace();
		}
	}
}

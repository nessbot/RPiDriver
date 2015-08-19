import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.EnumSet;
import java.util.HashMap;


/*


		  ----3----
		 /2/    /5/
		/ /    / /
	   ----6----
	  /1/   /4/
	 / /   / /
	----0----

 */

/**
 * Controls 7-segment LED when sending data in parellel.
 */
public class LedDigitParallel implements LedDigit {
	final GpioController gpio;
	GpioPinDigitalOutput[] leds = new GpioPinDigitalOutput[8];
	int[] segments;
	HashMap<Character, int[]> chars = new HashMap<Character, int[]>();
	int[][] nums;
	int[] ZERO = {0, 1, 2, 3, 4, 5};
	int[] ONE = {4, 5};
	int[] TWO = {3, 4, 1, 0, 6};
	int[] THREE = {3, 4, 6, 5, 0};
	int[] FOUR = {2, 6, 4, 5};
	int[] FIVE = {3, 0, 6, 2, 5};
	int[] SIX = {0, 1, 2, 3, 5, 6};
	int[] SEVEN = {3, 4, 5};
	int[] EIGHT = {0, 1, 2, 3, 4, 5, 6};
	int[] NINE = {2, 3, 4, 5, 6};
	int[] H = {1, 2, 6, 5, 4};
	int[] E = {0, 1, 2, 3, 6};
	int[] L = {0, 1, 2};
	int[] B = EIGHT;
	int[] O = ZERO;
	int[] F = {1, 2, 3, 6};
	int[] PERIOD = {7};
	int[] QUESTION = {3, 4, 6, 1};

	public LedDigitParallel(int[] pinNums, GpioController gpio) throws InterruptedException {
		if (pinNums == null) {
			int[] segs = {0, 1, 2, 3, 4, 5, 6, 7};
			this.segments = segs;
		} else {
			this.segments = pinNums;
		}
		setPins();                                       // initializes segment to display array
		this.gpio = gpio;                                // create gpio controller
		provisionsPins();
		clear();
	}

	public LedDigitParallel(GpioController gpio) throws InterruptedException {
		this(null, gpio);
	}

	/**
	 * Prints int digit to 7-segment LED
	 *
	 * @param n int - digit to print
	 */
	public void set(int n) {
		if (n > 9) {
			throw new IllegalArgumentException();
		}
		if (n < 0) {
			clear();
			return;
		}
		int[] num = nums[n];            //get array corresponding to entered number
		clear();
		for (int m : num) {
			leds[m].setState(PinState.HIGH);
		}

	}

	/**
	 * Prints legal character to LED digit
	 *
	 * @param c char - character to print
	 */
	public void set(char c) {
		Character b = c;
		//todo: replace with greater than or less than ASCI values
		if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0') {
			int a = Integer.parseInt(b.toString());
			set(a);
		}
		int[] ch = chars.get(c);
		clear();
		for (int m : ch) {
			leds[m].setState(true);
		}

	}

	/**
	 * @throws InterruptedException
	 */
	private void provisionsPins() throws InterruptedException {
		for (int i = 0; i < 8; i++) {
			GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(
					new PinImpl("RaspberryPi GPIO Provider", segments[i], "Seg_" + segments[i], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
					"MyLED",
					PinState.HIGH);
			java.lang.System.out.println("Initialized Pin " + segments[i]);
			Thread.sleep(100);

			leds[i] = pin;
			pin.setState(false);
		}
		blink(3, 200);
	}

	/**
	 * Displays LED spin at default speed
	 *
	 * @param n number of spins
	 * @throws InterruptedException
	 */
	public void spin(int n) throws InterruptedException {
		spin(n, 25);
	}

	/**
	 * DisplaysLED spin
	 *
	 * @param n number of spins
	 * @param t time between light-ups
	 * @throws InterruptedException
	 */
	public void spin(int n, int t) throws InterruptedException {
		clear();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 6; j++) {
				leds[j].setState(true);
				Thread.sleep(t);
				leds[j].setState(false);
			}
		}
		clear();
	}

	/**
	 * Blinks all segments of LED dispay
	 *
	 * @param n int - number of blinks
	 * @param t int - time between each blink in ms
	 * @throws InterruptedException
	 */
	public void blink(int n, int t) throws InterruptedException {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 8; j++) {
				leds[j].setState(true);

			}
			Thread.sleep(t);
			clear();
			Thread.sleep(t / 2);
		}
		clear();
	}

	/**
	 * Blink all segments of LED display at deault rate (1 blink per .1 seconds)
	 *
	 * @param n int - number of blinks
	 * @throws InterruptedException
	 */
	public void blink(int n) throws InterruptedException {
		blink(n, 100);
	}

	/**
	 * Set all LED segments to low
	 */
	public void clear() {
		for (GpioPinDigitalOutput pin : leds) {
			pin.setState(PinState.LOW);
		}
	}

	/**
	 * Writes string to display one character at a time
	 * @param s String - to print
	 * @throws InterruptedException
	 */
	public void write(String s) throws InterruptedException {
		char[] chars1 = s.toCharArray();
		for (Character c : chars1) {
			if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0') {
				int a = Integer.parseInt(c.toString());
				set(a);
			} else if (c == ' ') {
				clear();
			} else {
				set(c);
			}
			Thread.sleep(300);
			clear();
			Thread.sleep(30);
		}
	}

	/**
	 * Initialize arrays of segment labels for each possible input
	 */
	private void setPins() {
		nums = new int[][]{ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};
		chars.put('h', H);
		chars.put('e', E);
		chars.put('l', L);
		chars.put('o', O);
		chars.put('b', B);
		chars.put('f', F);
		chars.put('.', PERIOD);
		chars.put('?', QUESTION);
	}

}


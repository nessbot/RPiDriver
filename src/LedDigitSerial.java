import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by Steve on 2/8/2015.
 *
 * Controls 7-segment LED digit by sending data serially.
 */
public class LedDigitSerial implements LedDigit {
	GpioController gpio;
	GpioPinDigitalOutput dataIn;
	GpioPinDigitalOutput outputEnable;
	GpioPinDigitalOutput latch;
	GpioPinDigitalOutput clear;
	GpioPinDigitalOutput clock;
	HashMap<String, GpioPinDigitalOutput> outputs;
	int[] pinNums;
	ShiftRegister sr;
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
	private HashMap<Character, int[]> chars = new HashMap<Character, int[]>();

	public LedDigitSerial(int[] pinNums, GpioController gpio) throws InterruptedException {
		if (pinNums == null) {
			int[] temp = {1,4,5,3,2};
			this.pinNums = temp;
		} else {
			this.pinNums = pinNums;
		}
		this.gpio = gpio;                                // create gpio controller
		sr = new ShiftRegister(this.pinNums, gpio);
		provisionCharacters();
		provisionsPins();
		clear();
	}

	public LedDigitSerial(GpioController gpio) throws InterruptedException {
		this(null, gpio);
	}

	public void set(int n) throws InterruptedException {
		char c = Integer.toString(n).charAt(0);
		set(c);
	}

	public void set(char c) throws InterruptedException {
		sr.clear();
		ArrayList<Integer> bits = new ArrayList<Integer>();
		for (int i : chars.get(c)){
			bits.add(i);
		}
		boolean bit;
		for (int i = 0; i < 8; i++) {
			if (bits.contains(7-i)){
				bit = true;
			} else {
				bit = false;
			}
			sr.addBit(bit);
		}
		sr.push();
	}

	public void spin(int n) throws InterruptedException {
		spin(n, 100);
	}

	public void spin(int n, int t) throws InterruptedException {
		clear();
		for (int i = 0; i < n; i++) {
			sr.addBit(true);
			sr.push();
			Thread.sleep(t);
			for (int j = 0; j < 5; j++) {
				sr.addBit(false);
				sr.push();
				Thread.sleep(t);
			}
			sr.clear();
		}

	}

	public void blink(int n, int t) throws InterruptedException {
		for (int i = 0; i < n; i++) {
			set(8);
			Thread.sleep(t);
			clear();
			Thread.sleep(t*2/3);
		}
	}

	public void blink(int n) throws InterruptedException {
		blink(n, 100);
	}

	public void clear() throws InterruptedException {
		sr.clear();
	}

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

	private void provisionsPins(){
		dataIn = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pinNums[0], "Pin" + pinNums[0], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		outputEnable = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pinNums[1], "Pin" + pinNums[1], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		latch = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider",pinNums[2], "Pin" + pinNums[2], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		clear = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pinNums[3], "Pin" + pinNums[3], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.HIGH);
		clock = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pinNums[4], "Pin" + pinNums[4], EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
	}

	private void provisionCharacters(){
		chars.put('0', ZERO);
		chars.put('1', ONE);
		chars.put('2', TWO);
		chars.put('3', THREE);
		chars.put('4', FOUR);
		chars.put('5', FIVE);
		chars.put('6', SIX);
		chars.put('7', SEVEN);
		chars.put('8', EIGHT);
		chars.put('9', NINE);
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

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.EnumSet;

/**
 * Created by Steve on 2/8/2015.
 *
 * Controls shift register used for sending data serially.
 */
public class ShiftRegister {
	GpioController gpio;
	int[] pins;
	GpioPinDigitalOutput dataIn;
	GpioPinDigitalOutput outputEnable;
	GpioPinDigitalOutput latch;
	GpioPinDigitalOutput clear;
	GpioPinDigitalOutput clock;
	byte storageRegister;


	public ShiftRegister(int[] pins, GpioController gpio){
		this.pins = pins;
		this.gpio = gpio;
		provisionPins();
	}

	public void push() throws InterruptedException {
		latch.setState(false);
		latch.setState(true);
		latch.setState(false);
	}

	public void addBit(boolean bit) throws InterruptedException {
		clear.setState(true);
		clock.setState(false);
		if (bit){
			dataIn.setState(true);
		} else
			dataIn.setState(false);

		clock.setState(true);		// clock in bit
		clock.setState(false);		// turn off clock until next opersation
		dataIn.setState(false);
	}

	public void clear() throws InterruptedException {
		clear.setState(false);
		clear.setState(true);
		push();
	}

	public void addByte(byte b){

	}

	private void provisionPins(){
		System.out.println(gpio.toString());
		dataIn = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pins[0], "Pin1", EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		outputEnable = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pins[1], "Pin4", EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		latch = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider",pins[2], "Pin5", EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
		clear = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pins[3], "Pin3", EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.HIGH);
		clock = gpio.provisionDigitalOutputPin(
				new PinImpl("RaspberryPi GPIO Provider", pins[4], "Pin2", EnumSet.of(PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT), PinPullResistance.all()),
				"MyLED",
				PinState.LOW);
	}

}

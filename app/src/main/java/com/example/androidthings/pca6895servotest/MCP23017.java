package com.example.androidthings.pca6895servotest;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Created by mathew on 16/01/17.
 * Copyright 2017 Mathew Winters
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class MCP23017 implements Closeable {
  /**
   * Default IOCON state. Open-drain interrupts are used to allow any
   * voltage supported by the MCP23017 to be used on VDD.
   */
  private static final byte IOCON_BASE = 0b00000100;
  //private static final byte IOCON_MIRROR = 0b01000000; // NOSONAR

  // If initial bank is incorrect, then use 0x05
  private static final byte IOCON_ADDR = 0x0a;
  private static final byte A_TO_B_OFFSET = 0x01;
  private static final byte IODIR_ADDR = 0x00;
  private static final byte IPOL_ADDR = 0x02;
  //private static final byte GPINTEN_ADDR = 0x04; // NOSONAR
  // private static final byte DEFVAL_ADDR = 0x06; // NOSONAR
  private static final byte INTCON_ADDR = 0x08;
  private static final byte GPPU_ADDR = 0x0c;
  // private static final byte INTF_ADDR = 0x0e; // NOSONAR
  // private static final byte INTCAP_ADDR = 0x10; // NOSONAR
  private static final byte GPIO_ADDR = 0x12;
  // private static final byte OLAT_ADDR = 0x14; // NOSONAR
  private static final byte ZEROS = 0b00000000;
  private static final byte ONES = (byte) 0b11111111;

  private static final String TAG = "MCP23017";
  public static final String INVALID_PIN_NUMBER = "Invalid pin number";

  public enum MCPPinMode {
    MODE_OUTPUT, MODE_INPUT, MODE_INPUT_PULLUP
  }

  public enum MCPPinState {
    STATE_LOW, STATE_HIGH
  }

  // by default all outputs
  private MCPPinMode[] pinModes = new MCPPinMode[16];
  private I2cDevice i2cDevice;

  public MCP23017(byte address) throws IOException {
    PeripheralManagerService manager = new PeripheralManagerService();
    List<String> deviceList = manager.getI2cBusList();
    if (deviceList.isEmpty()) {
      i2cDevice = null;
      Log.i(TAG, "No I2C bus available on this device.");
    } else {
      Log.i(TAG, "List of available devices: " + deviceList);
      try {
        i2cDevice = manager.openI2cDevice(deviceList.get(0), address);
        if (i2cDevice != null) {
          Arrays.fill(pinModes, MCPPinMode.MODE_INPUT);
          i2cDevice.writeRegByte(IOCON_ADDR, IOCON_BASE );
          i2cDevice.writeRegByte(INTCON_ADDR, ZEROS);
          i2cDevice.writeRegByte(INTCON_ADDR + A_TO_B_OFFSET, ZEROS);
          i2cDevice.writeRegByte(IPOL_ADDR, ZEROS);
          i2cDevice.writeRegByte(IPOL_ADDR + A_TO_B_OFFSET, ZEROS);

        }
      } catch (IOException e) {
        Log.d(TAG, "IO Error " + e.getMessage());
        e.printStackTrace(); // NOSONAR
        throw e;
      }
    }
  }

  public MCP23017 setPinMode(int pin, MCPPinMode mode) throws IOException {
    if (pin < 0 || pin > 15)
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);
    pinModes[pin] = mode;
    int pinBit = pin % 8;
    int addrIodir = IODIR_ADDR + (pin > 7 ? A_TO_B_OFFSET : 0);
    if (mode == MCPPinMode.MODE_OUTPUT)
      i2cDevice.writeRegByte(addrIodir, (byte) ((i2cDevice.readRegByte(addrIodir) & 0xff) & (~(1 << pinBit))));
    else {
      i2cDevice.writeRegByte(addrIodir, (byte) ((i2cDevice.readRegByte(addrIodir) & 0xff) | (1 << pinBit)));

      int addrGppu = GPPU_ADDR + (pin > 7 ? A_TO_B_OFFSET : 0);
      if (mode == MCPPinMode.MODE_INPUT_PULLUP)
        i2cDevice.writeRegByte(addrIodir, (byte) ((i2cDevice.readRegByte(addrGppu) & 0xff) | (1 << pinBit)));
      else {
        i2cDevice.writeRegByte(addrIodir, (byte) ((i2cDevice.readRegByte(addrGppu) & 0xff) & (~(1 << pinBit))));
      }
    }
    return this;
  }

  public void setBulkPinModeBankA(MCPPinMode mode) throws IOException {

    for (int i = 0; i < 8; i++) {
      pinModes[i] = mode;
    }
    if (mode == MCPPinMode.MODE_OUTPUT)
      i2cDevice.writeRegByte(IODIR_ADDR, ZEROS);
    else {
      i2cDevice.writeRegByte(IODIR_ADDR, ONES);

      if (mode == MCPPinMode.MODE_INPUT_PULLUP)
        i2cDevice.writeRegByte(GPPU_ADDR, ONES);

      else {
        i2cDevice.writeRegByte(GPPU_ADDR, ZEROS);

      }
    }
  }

  public void setBulkPinModeBankB(MCPPinMode mode) throws IOException {

    for (int i = 8; i < 16; i++) {
      pinModes[i] = mode;
    }
    if (mode == MCPPinMode.MODE_OUTPUT)
      i2cDevice.writeRegByte(IODIR_ADDR + A_TO_B_OFFSET, ZEROS);
    else {
      i2cDevice.writeRegByte(IODIR_ADDR + A_TO_B_OFFSET, ONES);

      if (mode == MCPPinMode.MODE_INPUT_PULLUP)
        i2cDevice.writeRegByte(GPPU_ADDR + A_TO_B_OFFSET, ONES);

      else {
        i2cDevice.writeRegByte(GPPU_ADDR + A_TO_B_OFFSET, ZEROS);

      }
    }
  }

  public MCPPinState readPin(int pin) throws IOException {

    if (pin < 0 || pin > 15)
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);
    if (pinModes[pin] == MCPPinMode.MODE_OUTPUT) {
      throw new IllegalArgumentException("Pin is currently an output.");
    }

    int pinBit = pin % 8;
    int addrGpio = GPIO_ADDR + (pin > 7 ? A_TO_B_OFFSET : 0);
    byte gpioVal = i2cDevice.readRegByte(addrGpio);
    return (((gpioVal & 0xff) & (1 << pinBit)) != 0) ? MCPPinState.STATE_LOW : MCPPinState.STATE_HIGH;
  }

  public MCP23017 writePin(int pin, MCPPinState val) throws IOException {

    if (pin < 0 || pin > 15)
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);
    if (pinModes[pin] != MCPPinMode.MODE_OUTPUT) {
      throw new IllegalArgumentException("Pin is currently an input.");
    }

    int pinBit = pin % 8;
    int addrGpio = GPIO_ADDR + (pin > 7 ? A_TO_B_OFFSET : 0);
    if (val == MCPPinState.STATE_LOW) {
      i2cDevice.writeRegByte(addrGpio, (byte) ((i2cDevice.readRegByte(addrGpio) & 0xff) & (~(1 << pinBit))));
    } else {
      i2cDevice.writeRegByte(addrGpio, (byte) ((i2cDevice.readRegByte(addrGpio) & 0xff) | (1 << pinBit)));
    }

    return this;

  }

  @Override
  public void close() throws IOException {
    if (i2cDevice != null) {
      i2cDevice.close();
    }
  }

}

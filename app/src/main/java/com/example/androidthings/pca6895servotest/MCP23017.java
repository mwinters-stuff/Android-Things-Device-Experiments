package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;


/**
 * Created by mathew on 16/01/17.
 * Copyright 2017 Mathew Winters
 */

@SuppressWarnings({"WeakerAccess", "unused","squid:S00115", "squid:S1068"})
public class MCP23017 implements IODeviceInterface {

  // MCP23x17 Registers

  public static final String INVALID_PIN_NUMBER = "Invalid pin number";
  private static final int MCP23x17_IODIRA = 0x00;
  private static final int MCP23x17_IPOLA = 0x02;
  private static final int MCP23x17_GPINTENA = 0x04;
  private static final int MCP23x17_DEFVALA = 0x06;
  private static final int MCP23x17_INTCONA = 0x08;
  private static final int MCP23x17_IOCON = 0x0A;
  private static final int MCP23x17_GPPUA = 0x0C;
  private static final int MCP23x17_INTFA = 0x0E;
  private static final int MCP23x17_INTCAPA = 0x10;
  private static final int MCP23x17_GPIOA = 0x12;
  private static final int MCP23x17_OLATA = 0x14;
  private static final int MCP23x17_IODIRB = 0x01;
  private static final int MCP23x17_IPOLB = 0x03;
  private static final int MCP23x17_GPINTENB = 0x05;
  private static final int MCP23x17_DEFVALB = 0x07;
  private static final int MCP23x17_INTCONB = 0x09;
  private static final int MCP23x17_IOCONB = 0x0B;
  private static final int MCP23x17_GPPUB = 0x0D;
  private static final int MCP23x17_INTFB = 0x0F;
  private static final int MCP23x17_INTCAPB = 0x11;
  private static final int MCP23x17_GPIOB = 0x13;

  // Bits in the IOCON register
  private static final int MCP23x17_OLATB = 0x15;
  private static final int IOCON_UNUSED = 0x01;
  private static final int IOCON_INTPOL = 0x02;
  private static final int IOCON_ODR = 0x04;
  private static final int IOCON_HAEN = 0x08;
  private static final int IOCON_DISSLW = 0x10;
  private static final int IOCON_SEQOP = 0x20;
  private static final int IOCON_MIRROR = 0x40;

  // Default initialisation mode
  private static final int IOCON_BANK_MODE = 0x80;
  private static final int IOCON_INIT = IOCON_SEQOP;
  private static final String TAG = MCP23017.class.getName();
  private int data2;
  private int data3;
  private I2cDevice i2cDevice;


  public MCP23017(byte address, @NonNull PeripheralManagerService manager) throws IOException {
    List<String> deviceList = manager.getI2cBusList();
    if (deviceList.isEmpty()) {
      i2cDevice = null;
      Log.i(TAG, "No I2C bus available on this device.");
    } else {
      Log.i(TAG, "List of available devices: " + deviceList);
      try {
        i2cDevice = manager.openI2cDevice(deviceList.get(0), address);
        if (i2cDevice != null) {

          i2cDevice.writeRegByte(MCP23x17_IOCON, (byte) IOCON_INIT);

          data2 = i2cDevice.readRegByte(MCP23x17_OLATA);
          data3 = i2cDevice.readRegByte(MCP23x17_OLATB);

        }
      } catch (IOException e) {
        Log.d(TAG, "IO Error " + e.getMessage());
        e.printStackTrace(); // NOSONAR
        throw e;
      }
    }
  }

  @Override
  public void setPinMode(int pin, @NonNull PinMode mode) throws IOException {
    if (pin < 0 || pin > 15)
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);

    int mask;
    int old;
    int reg;
    int pinMasked = pin;

    if (pinMasked < 8)    // Bank A
      reg = MCP23x17_IODIRA;
    else {
      reg = MCP23x17_IODIRB;
      pinMasked &= 0x07;
    }

    mask = 1 << pinMasked;
    old = i2cDevice.readRegByte(reg);

    if (mode == PinMode.MODE_OUTPUT)
      old &= (~mask);
    else
      old |= mask;

    i2cDevice.writeRegByte(reg, (byte) old);

    if (mode != PinMode.MODE_OUTPUT) {
      if (pin < 8)    // Bank A
        reg = MCP23x17_GPPUA;
      else {
        reg = MCP23x17_GPPUB;
      }

      mask = 1 << pinMasked;
      old = i2cDevice.readRegByte(reg);

      if (mode == PinMode.MODE_INPUT_PULLUP)
        old |= mask;
      else
        old &= (~mask);

      i2cDevice.writeRegByte(reg, (byte) old);
    }

  }

  @Override
  @NonNull
  public PinState readPin(int pin) throws IOException {

    if (pin < 0 || pin > 15) {
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);
    }

    int mask;
    int value;
    int gpio;
    int pinMasked = pin;

    if (pinMasked < 8) {    // Bank A
      gpio = MCP23x17_GPIOA;
    } else {
      gpio = MCP23x17_GPIOB;
      pinMasked &= 0x07;
    }

    mask = 1 << pinMasked;
    value = i2cDevice.readRegByte(gpio);

    if ((value & mask) == 0) {
      return PinState.LOW;
    } else {
      return PinState.HIGH;
    }
  }

  @Override
  public void writePin(int pin, @NonNull PinState value) throws IOException {

    if (pin < 0 || pin > 15) {
      throw new IllegalArgumentException(INVALID_PIN_NUMBER);
    }
    int bit = 1 << (pin & 7);

    if (pin < 8) {

      int old = data2;

      if (value == PinState.LOW) {
        old &= (~bit);
      } else {
        old |= bit;
      }

      i2cDevice.writeRegByte(MCP23x17_GPIOA, (byte) old);
      data2 = old;
    } else {
      int old = data3;

      if (value == PinState.LOW)
        old &= (~bit);
      else
        old |= bit;

      i2cDevice.writeRegByte(MCP23x17_GPIOB, (byte) old);
      data3 = old;
    }
  }

  @Override
  public void setPwmFreq(int freqHz) throws IOException {
    throw new UnsupportedOperationException("setPwmFreq on MCP23017");
  }

  @Override
  public void setAllPwm(int on, int off) throws IOException {
    throw new UnsupportedOperationException("setAllPwm on MCP23017");
  }

  @Override
  public void setPwm(int channel, int on, int off) throws IOException {
    throw new UnsupportedOperationException("setPwm on MCP23017");
  }

  @Override
  public void close() throws IOException {
    if (i2cDevice != null) {
      i2cDevice.close();
    }
  }



}

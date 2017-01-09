package com.example.androidthings.pca6895ServoTest;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;

/**
 * Created by mathew on 6/01/17.
 * Copyright 2017 Mathew Winters
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class PCA9685Servo {
  public static final byte PCA9685_ADDRESS = 0x40;
  private static final int MODE1 = 0x00;
  private static final int MODE2 = 0x01;
  private static final int SUBADR1 = 0x02;
  private static final int SUBADR2 = 0x03;
  private static final int SUBADR3 = 0x04;
  private static final int PRESCALE = 0xFE;
  private static final int LED0_ON_L = 0x06;
  private static final int LED0_ON_H = 0x07;
  private static final int LED0_OFF_L = 0x08;
  private static final int LED0_OFF_H = 0x09;
  private static final int ALL_LED_ON_L = 0xFA;
  private static final int ALL_LED_ON_H = 0xFB;
  private static final int ALL_LED_OFF_L = 0xFC;
  private static final int ALL_LED_OFF_H = 0xFD;
  //Bits:;
  private static final int RESTART = 0x80;
  private static final int SLEEP = 0x10;
  private static final int ALLCALL = 0x01;
  private static final int INVRT = 0x10;
  private static final int OUTDRV = 0x04;

  private static final String TAG="Servo";

  private I2cDevice i2cDevice;
  private int minPwm;
  private int maxPwm;
  private int minAngle;
  private int maxAngle;
  private int currentPwm;


  public PCA9685Servo(byte address) {
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

          setAllPwm(0, 0);
          i2cDevice.writeRegByte(MODE2, (byte) OUTDRV);
          i2cDevice.writeRegByte(MODE1, (byte) ALLCALL);
          Thread.sleep(5); // #wait for oscillator
          byte mode1 = i2cDevice.readRegByte(MODE1);
          mode1 = (byte) (mode1 & ~SLEEP); //#wake up (reset sleep)
          i2cDevice.writeRegByte(MODE1, mode1);
          Thread.sleep(5); //#wait for oscillator


          setPwmFreq(50); // good default.
        }
      } catch (IOException e) {
        Log.d(TAG, "IO Error " + e.getMessage());
        e.printStackTrace();
        throw e;
      } catch (InterruptedException e) {
        Log.d(TAG, "Error in sleep " + e.getMessage());
        e.printStackTrace();
        throw e;
      }
    }
  }



  public void destroy(){
    if(i2cDevice != null){
      try {
        i2cDevice.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    }

  }

  public void setServoMinMaxPwm(int minAngle, int maxAngle, int minPwm, int maxPwm){
    this.maxPwm = maxPwm;
    this.minPwm = minPwm;
    this.minAngle = minAngle;
    this.maxAngle = maxAngle;
  }


  public void setServoAngle(int channel, int angle){
    currentPwm = map(angle,minAngle,maxAngle,minPwm,maxPwm);
    setPwm(channel,0,currentPwm);
  }

  public void setPwmFreq(int freq_hz) {
    try {
      double prescaleval = 25000000.0;    //# 25MHz
      prescaleval /= 4096.0;       //# 12-bit
      prescaleval /= freq_hz;
      prescaleval -= 1.0;

      Log.d(TAG, String.format("Setting PWM frequency to %d Hz", freq_hz));
      Log.d(TAG, String.format("Estimated pre-scale: %.4f", prescaleval));
      int prescale = (int) Math.floor(prescaleval + 0.5);
      Log.d(TAG, String.format("Final pre-scale: %d", prescale));
      byte oldmode = i2cDevice.readRegByte(MODE1);
      byte newmode = (byte) ((oldmode & 0x7F) | 0x10); //#sleep
      i2cDevice.writeRegByte(MODE1, newmode); //#go to sleep
      i2cDevice.writeRegByte(PRESCALE, (byte) prescale);
      i2cDevice.writeRegByte(MODE1, oldmode);

      Thread.sleep(5);

      i2cDevice.writeRegByte(MODE1, (byte) (oldmode | 0x80));
    } catch (IOException e) {
      Log.d(TAG, "IO Error " + e.getMessage());
      e.printStackTrace();
      throw e;
    } catch (InterruptedException e) {
      Log.d(TAG, "Error in sleep " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public void setPwm(int channel, int on, int off) {
    if (i2cDevice != null) {
      try {
        i2cDevice.writeRegByte(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        i2cDevice.writeRegByte(LED0_ON_H + 4 * channel, (byte) (on >> 8));
        i2cDevice.writeRegByte(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
        i2cDevice.writeRegByte(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    }

  }

  public void setAllPwm(int on, int off) {
    if (i2cDevice != null) {
      try {
        i2cDevice.writeRegByte(ALL_LED_ON_L, (byte) (on & 0xFF));
        i2cDevice.writeRegByte(ALL_LED_ON_H, (byte) (on >> 8));
        i2cDevice.writeRegByte(ALL_LED_OFF_L, (byte) (off & 0xFF));
        i2cDevice.writeRegByte(ALL_LED_OFF_H, (byte) (off >> 8));
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    }
  }


  public int map(int x, int in_min, int in_max, int out_min, int out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }

  public int getCurrentPwm(){
    return currentPwm;
  }
}

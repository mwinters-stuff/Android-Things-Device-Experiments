package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;

/**
 * @author MWinters
 */
public class GPIODevice implements IODeviceInterface {
  private static final String TAG = GPIODevice.class.getName();
  List<String> portList;

  public GPIODevice(@NonNull PeripheralManagerService manager) throws IOException {
    portList = manager.getGpioList();
    if (portList.isEmpty()) {
      Log.i(TAG, "No GPIO port available on this device.");
      throw new IOException("No GPIO Port");
    }
    Log.i(TAG, "List of available ports: " + portList);

  }


    @Override
  public void setPinMode(int pin, @NonNull PinMode mode) throws IOException {

  }

  @NonNull
  @Override
  public PinState readPin(int pin) throws IOException {
    return null;
  }

  @Override
  public void writePin(int pin, @NonNull PinState value) throws IOException {

  }

  @Override
  public void setPwmFreq(int freqHz) throws IOException {

  }

  @Override
  public void setAllPwm(int on, int off) throws IOException {

  }

  @Override
  public void setPwm(int channel, int on, int off) throws IOException {

  }

  @Override
  public void close() throws IOException {
//    if (device != null) {
//      device.close();
//      device = null;
//    }
  }
}

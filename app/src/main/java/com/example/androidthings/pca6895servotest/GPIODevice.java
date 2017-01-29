package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author MWinters
 */
public class GPIODevice implements IODeviceInterface {
  private static final String TAG = GPIODevice.class.getName();
  List<String> portList;
  SparseArray<Gpio> gpioPorts = new SparseArray<>();

  public GPIODevice(@NonNull PeripheralManagerService manager) throws IOException {
    portList = manager.getGpioList();
    if (portList.isEmpty()) {
      Log.i(TAG, "No GPIO port available on this device.");
      throw new IOException("No GPIO Port");
    }
    Log.i(TAG, "List of available ports: " + portList);

    for (String port : portList) {
      int num = Integer.parseInt(port.replace("BCM", ""));
      gpioPorts.put(num, manager.openGpio(port));
    }

  }


  @Override
  public void setPinMode(int pin, @NonNull PinMode mode) throws IOException {
    Gpio gpio = gpioPorts.get(pin, null);
    if (gpio != null) {
      switch (mode) {
        case MODE_OUTPUT:
          gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
          gpio.setActiveType(Gpio.ACTIVE_HIGH);
          break;
        case MODE_INPUT:
        case MODE_INPUT_PULLUP:
        case MODE_INPUT_PULLDOWN:
          gpio.setDirection(Gpio.DIRECTION_IN);
          gpio.setActiveType(Gpio.ACTIVE_HIGH);
          break;
      }
    }else{
      throw new IOException("Unknown Pin " + pin);
    }
  }

  @NonNull
  @Override
  public PinState readPin(int pin) throws IOException {
    Gpio gpio = gpioPorts.get(pin, null);
    if (gpio != null) {
      return gpio.getValue() ? PinState.HIGH : PinState.LOW;
    }
    throw new IOException("Unknown Pin " + pin);
  }

  @Override
  public void writePin(int pin, @NonNull PinState value) throws IOException {
    Gpio gpio = gpioPorts.get(pin, null);
    if (gpio != null) {
      gpio.setValue(value == PinState.HIGH);
    }else {
      throw new IOException("Unknown Pin " + pin);
    }
  }

  @Override
  public void setPwmFreq(int freqHz) throws IOException {
    throw new UnsupportedOperationException("GPIODevice setPwmFreq");
  }

  @Override
  public void setAllPwm(int on, int off) throws IOException {
    throw new UnsupportedOperationException("GPIODevice setAllPwm");
  }

  @Override
  public void setPwm(int channel, int on, int off) throws IOException {
    throw new UnsupportedOperationException("GPIODevice setPwm");
  }

  @Override
  public void close() throws IOException {
    for (int p = 0; p < 40; p++) {
      Gpio gpio = gpioPorts.get(p, null);
      if (gpio != null) {
        gpio.close();
      }
    }
  }
}

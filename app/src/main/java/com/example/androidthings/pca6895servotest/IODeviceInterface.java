package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;

import com.google.android.things.pio.PeripheralManagerService;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mathew on 18/01/17.
 * Copyright 2017 Mathew Winters
 */

public interface IODeviceInterface extends Closeable {
  //IODeviceInterface(@NonNull PeripheralManagerService manager);

  enum PinMode {
    MODE_OUTPUT, MODE_INPUT, MODE_INPUT_PULLUP, MODE_INPUT_PULLDOWN
  }

  enum PinState {
    LOW, HIGH
  }


  void setPinMode(int pin, @NonNull PinMode mode) throws IOException;
  @NonNull PinState readPin(int pin) throws IOException;
  void writePin(int pin, @NonNull PinState value) throws IOException;

  void setPwmFreq(int freqHz) throws IOException;
  void setAllPwm(int on, int off) throws IOException;
  void setPwm(int channel, int on, int off) throws IOException;


  }



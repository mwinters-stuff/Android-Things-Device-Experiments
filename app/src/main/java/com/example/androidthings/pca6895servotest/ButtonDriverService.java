package com.example.androidthings.pca6895servotest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.google.android.things.userdriver.InputDriver;
import com.google.android.things.userdriver.UserDriverManager;

import java.io.IOException;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */


public class ButtonDriverService extends Service {
  private static final String DRIVER_NAME = "AdafruitButtons";
  private static final String TAG = ButtonDriverService.DRIVER_NAME;
  private static final int DRIVER_VERSION = 1;

  private static final int[] KEY_CODES = {KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4};

  private InputDriver inputDriver;
  private Thread thread;
  private boolean keepRunning;


  @Override
  public void onCreate() {
    super.onCreate();

    inputDriver = InputDriver.builder(InputDevice.SOURCE_CLASS_BUTTON)
        .setName(DRIVER_NAME)
        .setVersion(DRIVER_VERSION)
        .setKeys(KEY_CODES)
        .build();

    UserDriverManager manager = UserDriverManager.getManager();
    manager.registerInputDriver(inputDriver);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    keepRunning = false;

    UserDriverManager manager = UserDriverManager.getManager();
    manager.unregisterInputDriver(inputDriver);
  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    keepRunning = true;
    thread = new Thread(new Runnable() {
      MCP23017.PinState[] pinStates = new MCP23017.PinState[5];

      @Override
      public void run() {
        try {
          DeviceHolder deviceHolder = DeviceHolder.getInstance();
          Log.d(TAG,"Starting service thread");
          for (int i = 0; i <= 4; ++i) {
            deviceHolder.getDeviceMCP23017().setPinMode(i, MCP23017.PinMode.MODE_INPUT_PULLUP);
            pinStates[i] = deviceHolder.getDeviceMCP23017().readPin(i);
          }

          while (keepRunning) {
            for (int i = 0; i < 5; i++) {
              MCP23017.PinState state = deviceHolder.getDeviceMCP23017().readPin((byte) i);
              if (state != pinStates[i]) {
                Log.d(TAG,"State change " + i + state);
                triggerEvent(i, state == MCP23017.PinState.LOW);
                pinStates[i] = state;
                Thread.sleep(10);
              }
            }
            Thread.sleep(1);

          }
          Log.d(TAG,"Thread stopping");

        } catch (IOException e) { /// NOSONAR
          e.printStackTrace();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

    });
    thread.start();


    return START_STICKY;

  }

  private void triggerEvent(int input, boolean pressed) {
    int action = pressed ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
    KeyEvent[] events = new KeyEvent[]{new KeyEvent(action, KEY_CODES[input])};

    if (!inputDriver.emit(events)) {
      Log.w(TAG, "Unable to emit key event");
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}

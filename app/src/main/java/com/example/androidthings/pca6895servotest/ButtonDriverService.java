package com.example.androidthings.pca6895servotest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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

@SuppressWarnings({"squid:S3776","squid:MethodCyclomaticComplexity","squid:S1151"})
public class ButtonDriverService extends Service {
  private static final String DRIVER_NAME = "AdafruitButtons";
  private static final String TAG = ButtonDriverService.DRIVER_NAME;
  private static final int DRIVER_VERSION = 1;

  private static final int[] KEY_CODES = {KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4};

  private InputDriver inputDriver;
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



  private class PinReader implements Runnable {
    private long[] prevDownTimeMillis = {-1L,-1L,-1L,-1L,-1L};
    private boolean[] maskDown = {false,false,false,false,false};
    private int[] stateDown = {0,0,0,0,0};
    IODeviceInterface.PinState[] pinStates = new IODeviceInterface.PinState[5];

    @Override
    @SuppressWarnings("squid:S134")
    public void run() {
      try {
        DeviceHolder deviceHolder = DeviceHolder.getInstance();
        IODeviceInterface deviceInterface = deviceHolder.getDevice(DeviceHolder.Devices.MCP23017);
        if (deviceInterface != null) {

          Log.d(TAG, "Starting service thread");
          for (int i = 0; i <= 4; ++i) {
            deviceInterface.setPinMode(i, IODeviceInterface.PinMode.MODE_INPUT_PULLUP);
            pinStates[i] = deviceInterface.readPin(i);
          }


          while (keepRunning) {
            for (int i = 0; i < 5; i++) {
              if (checkButtonPressDebounced(deviceInterface, i)) {
                Log.d(TAG, "State change down " + i);
                triggerEvent(i, true);
                triggerEvent(i, false);

              }
            }
            Thread.sleep(1);

          }
        }
        Log.d(TAG, "Thread stopping");

      } catch (IOException e) { // NOSONAR
        e.printStackTrace(); // NOSONAR
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    private boolean checkButtonPressDebounced(@NonNull IODeviceInterface deviceInterface, int pin) throws IOException{
      long timeMillis = SystemClock.uptimeMillis();
      switch(stateDown[pin]){
        case 0:
          if(deviceInterface.readPin(pin) == IODeviceInterface.PinState.LOW){
            maskDown[pin] = false;
            prevDownTimeMillis[pin] = timeMillis;
            stateDown[pin] = 1;
            Log.d(TAG,"Down state 0 to 1");

          }
          break;
        case 1:
          if(timeMillis - prevDownTimeMillis[pin] >= 15){
            if(deviceInterface.readPin(pin) == IODeviceInterface.PinState.LOW){
              Log.d(TAG,"Down state 1 to 2");
              stateDown[pin] = 2;
            }else{
              Log.d(TAG,"Down state 1 to 0");
              stateDown[pin] = 0;
            }
          }
          break;
        case 2:
          if(deviceInterface.readPin(pin) == IODeviceInterface.PinState.HIGH){
            stateDown[pin] = 3;
            Log.d(TAG,"Down state 2 to 3");
            maskDown[pin] = true;
            prevDownTimeMillis[pin] = timeMillis;
          }else if(maskDown[pin] != (deviceInterface.readPin(pin) == IODeviceInterface.PinState.HIGH)){
            stateDown[pin] = 0;
            Log.d(TAG,"Down state 2 to 0");
          }
          break;
        case 3:
          if(timeMillis - prevDownTimeMillis[pin] >= 15) {
            if (deviceInterface.readPin(pin) == IODeviceInterface.PinState.HIGH) {
              stateDown[pin] = 0;
              Log.d(TAG,"Down state 3 to 0 XXXX");
              return true;
            }else{
              Log.d(TAG,"Down state 3 to 2");
              stateDown[pin] = 2;
            }
          }
          break;
        default:
          break;
      }
      return false;
    }

    private void triggerEvent(int input, boolean pressed) {
      int action = pressed ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
      KeyEvent[] events = new KeyEvent[]{new KeyEvent(action, KEY_CODES[input])};

      if (!inputDriver.emit(events)) {
        Log.w(TAG, "Unable to emit key event");
      }
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    keepRunning = true;
    Thread thread = new Thread(new PinReader());
    thread.start();
    return START_STICKY;

  }



  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}

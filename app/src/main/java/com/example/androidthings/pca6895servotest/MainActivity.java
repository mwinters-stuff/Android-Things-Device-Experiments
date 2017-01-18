/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.pca6895servotest;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.things.pio.PeripheralManagerService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;


/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 */

@EActivity(R.layout.main_activity)
public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Pref
  AppPrefs_ appPrefs;

  @ViewById(R.id.seekBar)
  RangeBar rangeBar;

  @ViewById(R.id.textView)
  TextView textView;

  @ViewById(R.id.spinnerChannel)
  Spinner spinnerChannel;

  private static final int SERVO_MIN = 145;
  private static final int SERVO_MAX = 580;
  private int usingChannel = 0;


  class RangeBarChangeListener implements RangeBar.OnRangeBarChangeListener {

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
      try {
        appPrefs.angleLeft().put(leftPinIndex);
        appPrefs.angleRight().put(rightPinIndex);
        updateText();
      } catch (Exception e) { // NOSONAR
        Log.d("ERROR", "Exception: " + e.getMessage());
      }

    }
  }

  static final int AF_RED  = 6;
  static final int AF_GREEN = 7;
  static final int AF_BLUE  = 8;

  static final int AF_E =   13;
  static final int AF_RW =   14;
  static final int AF_RS =   15;

  static final int AF_DB4 =   12;
  static final int AF_DB5 =   11;
  static final int AF_DB6 =   10;
  static final int AF_DB7 =  9;

  private LCDDriver lcdDriver;

  static final int AF_SELECT =  0;
//  static final int AF_RIGHT =  1;
//  static final int AF_DOWN =  2;
//  static final int AF_UP =  3;
//  static final int AF_LEFT =  4;

  enum ServoPosition{
    LEFT,RIGHT
  }

  private ServoPosition[] servoPositions = {ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT};

  private DeviceHolder deviceHolder = DeviceHolder.getInstance();

  @AfterViews
  protected void onAfterViews() {
    rangeBar.setRangePinsByIndices(appPrefs.angleLeft().get(),appPrefs.angleRight().get());
    rangeBar.setOnRangeBarChangeListener(new RangeBarChangeListener());
    spinnerChannel.setSelection(appPrefs.selectedChannel().get());

    try {
      PeripheralManagerService peripheralManagerService = new PeripheralManagerService();

      @SuppressWarnings("squid:S2095")
      PCA9685Servo pca9685Servo = new PCA9685Servo(PCA9685Servo.PCA9685_ADDRESS, peripheralManagerService);
      pca9685Servo.setServoMinMaxPwm(0, 180, SERVO_MIN, SERVO_MAX);
      deviceHolder.setDevice(DeviceHolder.Devices.PCA9685SERVO,pca9685Servo);

      @SuppressWarnings("squid:S2095")
      MCP23017 mcp23017 = new MCP23017((byte) 0x20, peripheralManagerService);


      mcp23017.setPinMode(AF_RED, MCP23017.PinMode.MODE_OUTPUT);
      mcp23017.setPinMode(AF_GREEN, MCP23017.PinMode.MODE_OUTPUT);
      mcp23017.setPinMode(AF_BLUE, MCP23017.PinMode.MODE_OUTPUT);
      mcp23017.writePin(AF_RED, MCP23017.PinState.HIGH);
      mcp23017.writePin(AF_GREEN, MCP23017.PinState.HIGH);
      mcp23017.writePin(AF_BLUE, MCP23017.PinState.HIGH);

      deviceHolder.setDevice(DeviceHolder.Devices.MCP23017, mcp23017);

      doSelect();

      mcp23017.setPinMode(AF_RW, MCP23017.PinMode.MODE_OUTPUT);
      mcp23017.writePin(AF_RW, MCP23017.PinState.LOW);
      lcdDriver = new LCDDriver(mcp23017,2,16,4,AF_RS,AF_E,AF_DB4,AF_DB5,AF_DB6,AF_DB7,0,0,0,0);

      lcdDriver.lcdPuts("Hello");

    } catch (Exception e) { // NOSONAR
      Log.d("ERROR", "Exception: " + e.getMessage());
    }
  }

  @Override
  public void onResume(){
    super.onResume();

    Intent intent = new Intent(this,ButtonDriverService.class);
    startService(intent);
  }

  @Override
  public void onPause(){
    super.onPause();
    Intent intent = new Intent(this,ButtonDriverService.class);
    stopService(intent);
  }

  @Click(R.id.buttonSetLeft)
  void onButtonSetLeftClick(){
    try {
      IODeviceInterface deviceInterface = DeviceHolder.getInstance().getDevice(DeviceHolder.Devices.PCA9685SERVO);
      if(deviceInterface != null && deviceInterface instanceof PCA9685Servo) {
        ((PCA9685Servo)deviceInterface).setServoAngle(usingChannel, appPrefs.angleLeft().get());
        servoPositions[usingChannel] = ServoPosition.LEFT;
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Left Click: " + e.getMessage());
    }
  }

  @Click(R.id.buttonSetRight)
  void onButtonSetRightClick(){
    try {
      IODeviceInterface deviceInterface = DeviceHolder.getInstance().getDevice(DeviceHolder.Devices.PCA9685SERVO);
      if(deviceInterface != null && deviceInterface instanceof PCA9685Servo) {
        ((PCA9685Servo)deviceInterface).setServoAngle(usingChannel, appPrefs.angleRight().get());
        servoPositions[usingChannel] = ServoPosition.RIGHT;
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Right Click: " + e.getMessage());
    }
  }

  @ItemSelect(R.id.spinnerChannel)
  void onItemSelect(boolean selected, int position){
    if(selected) {
      usingChannel = position;
      appPrefs.selectedChannel().put(position);
      updateText();
    }
  }

  private void updateText() {
    textView.setText(String.format(Locale.getDefault(), "Channel %d Angle Left %d Angle Right %d",
        usingChannel, rangeBar.getLeftIndex(), rangeBar.getRightIndex()));

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

  @KeyDown({KeyEvent.KEYCODE_0,KeyEvent.KEYCODE_1,KeyEvent.KEYCODE_2,KeyEvent.KEYCODE_3,KeyEvent.KEYCODE_4})
  void onKeyDown0(KeyEvent keyEvent){
    int channel =keyEvent.getKeyCode() - KeyEvent.KEYCODE_0;
    Log.d(TAG,"Key Down " + channel);
    try {
      if(channel == AF_SELECT){
        doSelect();
      }else{
        swapChannel(channel-1);
      }

    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Right Click: " + e.getMessage());
    }
  }

  int currentColour = 0;

  class Rgb{
    Rgb(boolean r, boolean g, boolean b){
      this.r = r;
      this.g = g;
      this.b = b;
    }
    final boolean  r;
    final boolean  g;
    final boolean  b;
  }

  Rgb[] rbgs = {
      new Rgb(true,false,false),
      new Rgb(false,true,false),
      new Rgb(false,false,true),
      new Rgb(true,true,true),
      new Rgb(true,false,true),
      new Rgb(false,true,true),
      new Rgb(true,true,false),
  };

  private void doSelect() throws IOException {
    IODeviceInterface deviceInterface = DeviceHolder.getInstance().getDevice(DeviceHolder.Devices.MCP23017);

    deviceInterface.writePin(AF_RED, rbgs[currentColour].r ? IODeviceInterface.PinState.LOW :  IODeviceInterface.PinState.HIGH);
    deviceInterface.writePin(AF_GREEN, rbgs[currentColour].g ? IODeviceInterface.PinState.LOW :  IODeviceInterface.PinState.HIGH);
    deviceInterface.writePin(AF_BLUE, rbgs[currentColour].b ? IODeviceInterface.PinState.LOW :  IODeviceInterface.PinState.HIGH);

    currentColour ++;
    if(currentColour == 7){
      currentColour = 0;
    }

  }

  private void swapChannel(int channel) throws IOException {
    IODeviceInterface deviceInterface = DeviceHolder.getInstance().getDevice(DeviceHolder.Devices.PCA9685SERVO);
    if(deviceInterface != null && deviceInterface instanceof PCA9685Servo) {
      if(servoPositions[channel] == ServoPosition.LEFT) {
        ((PCA9685Servo)deviceInterface).setServoAngle(channel, appPrefs.angleRight().get());
        servoPositions[channel] = ServoPosition.RIGHT;
        lcdDriver.lcdClear();
        lcdDriver.lcdPuts("CH " + channel + " RIGHT");
      }else{
        ((PCA9685Servo)deviceInterface).setServoAngle(channel, appPrefs.angleLeft().get());
        servoPositions[channel] = ServoPosition.LEFT;
        lcdDriver.lcdClear();
        lcdDriver.lcdPuts("CH " + channel + " LEFT");
      }
    }
  }
}

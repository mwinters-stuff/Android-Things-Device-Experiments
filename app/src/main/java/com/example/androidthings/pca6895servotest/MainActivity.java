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
import android.util.Log;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.things.pio.PeripheralManagerService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
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

  @ViewById(R.id.seekBar)
  RangeBar rangeBar;

  @ViewById(R.id.textView)
  TextView textView;

  private static final int SERVO_MIN = 145;
  private static final int SERVO_MAX = 580;
  private int usingChannel = 0;

  private int leftAngle = 0;
  private int rightAngle = 100;

  private PCA9685Servo pca9685Servo;
  private MCP23017 mcp23017;

  class RangeBarChangeListener implements RangeBar.OnRangeBarChangeListener {

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
      try {
        leftAngle = leftPinIndex;
        rightAngle = rightPinIndex;

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

  static final int AF_SELECT =  0;
  static final int AF_RIGHT =  1;
  static final int AF_DOWN =  2;
  static final int AF_UP =  3;
  static final int AF_LEFT =  4;



  @AfterViews
  protected void onAfterViews() {
    rangeBar.setOnRangeBarChangeListener(new RangeBarChangeListener());

    try {
      pca9685Servo = new PCA9685Servo(PCA9685Servo.PCA9685_ADDRESS, new PeripheralManagerService());
      pca9685Servo.setServoMinMaxPwm(0, 180, SERVO_MIN, SERVO_MAX);

      mcp23017 = new MCP23017((byte)0x20);


      mcp23017.setPinMode(AF_RED, MCP23017.MCPPinMode.MODE_OUTPUT);
      mcp23017.setPinMode(AF_GREEN, MCP23017.MCPPinMode.MODE_OUTPUT);
      mcp23017.setPinMode(AF_BLUE, MCP23017.MCPPinMode.MODE_OUTPUT);
      mcp23017.writePin(AF_RED, MCP23017.MCPPinState.STATE_HIGH);
      mcp23017.writePin(AF_GREEN, MCP23017.MCPPinState.STATE_HIGH);
      mcp23017.writePin(AF_BLUE, MCP23017.MCPPinState.STATE_HIGH);


      for (int i = 0; i <= 4; ++i) {
        mcp23017.setPinMode(i, MCP23017.MCPPinMode.MODE_INPUT_PULLUP);
      }



      mcp23017.writePin(AF_RED, MCP23017.MCPPinState.STATE_LOW);
      mcp23017.writePin(AF_GREEN, MCP23017.MCPPinState.STATE_LOW);
      mcp23017.writePin(AF_BLUE, MCP23017.MCPPinState.STATE_LOW);

    } catch (Exception e) { // NOSONAR
      Log.d("ERROR", "Exception: " + e.getMessage());
    }
  }

  @Click(R.id.buttonSetLeft)
  void onButtonSetLeftClick(){
    try {
      if(pca9685Servo != null) {
        pca9685Servo.setServoAngle(usingChannel, leftAngle);
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Left Click: " + e.getMessage());
    }
  }

  @Click(R.id.buttonSetRight)
  void onButtonSetRightClick(){
    try {
      if(pca9685Servo != null) {
        pca9685Servo.setServoAngle(usingChannel, rightAngle);
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Right Click: " + e.getMessage());
    }
  }

  @ItemSelect(R.id.spinnerChannel)
  void onItemSelect(boolean selected, int position){
    if(selected) {
      usingChannel = position;
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

}

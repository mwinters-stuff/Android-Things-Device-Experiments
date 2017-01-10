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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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
public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  private SeekBar seekBar;
  private TextView textView;

  private static final int SERVO_MIN = 145;
  private static final int SERVO_MAX = 580;
  private int usingChannel = 0;

  private PCA9685Servo pca9685Servo;
  @SuppressWarnings("FieldCanBeLocal")
  private Spinner spinnerChannel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.main_activity);

    seekBar = (SeekBar) findViewById(R.id.seekBar);
    textView = (TextView) findViewById(R.id.textView);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateText();
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // ignoreing this.
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        try {
          pca9685Servo.setServoAngle(usingChannel, seekBar.getProgress());
          updateText();
        } catch (Exception e) {
          Log.d("ERROR", "Exception: " + e.getMessage());
        }
      }
    });

    spinnerChannel = (Spinner) findViewById(R.id.spinnerChannel);
    spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        usingChannel = position;
        updateText();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // not implemented or needed
      }
    });

    try {
      pca9685Servo = new PCA9685Servo(PCA9685Servo.PCA9685_ADDRESS);
      pca9685Servo.setServoMinMaxPwm(0, 180, SERVO_MIN, SERVO_MAX);
    } catch (Exception e) {
      Log.d("ERROR", "Exception: " + e.getMessage());
    }

  }

  private void updateText() {
    textView.setText(String.format(Locale.getDefault(), "Channel %d Angle %d pwm %d", usingChannel, seekBar.getProgress(), pca9685Servo.getCurrentPwm()));

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

}

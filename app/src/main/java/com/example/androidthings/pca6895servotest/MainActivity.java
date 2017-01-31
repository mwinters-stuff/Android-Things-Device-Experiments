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
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.example.androidthings.pca6895servotest.rf24.RF24;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.common.base.Joiner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  static {
    System.loadLibrary("native-lib");
  }

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
  //private GPIODevice gpioDevice;
  private Gpio cePin;
  private Gpio ledPinRed;


  class RangeBarChangeListener implements RangeBar.OnRangeBarChangeListener {

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
      try {
        setChannelLeftAngle(usingChannel, leftPinIndex);
        setChannelRightAngle(usingChannel, rightPinIndex);
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

  private List<Integer> channelLeftAngles = new ArrayList<>(16);
  private List<Integer> channelRightAngles = new ArrayList<>(16);

  private ServoPosition[] servoPositions = {ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT,ServoPosition.LEFT};

  private DeviceHolder deviceHolder = DeviceHolder.getInstance();



  @AfterViews
  protected void onAfterViews() {
    channelLeftAngles.clear();
    if(!appPrefs.channelAnglesLeft().get().isEmpty()){
      TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter('|');

      // Once per string to split
      splitter.setString(appPrefs.channelAnglesLeft().get());
      for (String s : splitter) {
        channelLeftAngles.add(Integer.parseInt(s));
      }
    }else{
      for(int i = 0; i < 16; i++){
        channelLeftAngles.add(20);
      }
    }

    if(!appPrefs.channelAnglesRight().get().isEmpty()){
      channelRightAngles.clear();
      TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter('|');

      // Once per string to split
      splitter.setString(appPrefs.channelAnglesRight().get());
      for (String s : splitter) {
        channelRightAngles.add(Integer.parseInt(s));
      }
    }else{
      for(int i = 0; i < 16; i++){
        channelRightAngles.add(40);
      }
    }

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

      cePin = peripheralManagerService.openGpio("BCM22");
      ledPinRed = peripheralManagerService.openGpio("BCM13");
      ledPinRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
      ledPinRed.setValue(false);

//      gpioDevice = new GPIODevice(peripheralManagerService);
//
//      gpioDevice.setPinMode(13, IODeviceInterface.PinMode.MODE_OUTPUT);
//      gpioDevice.writePin(13, IODeviceInterface.PinState.LOW);

      pingRadioThread(peripheralManagerService);

    } catch (Exception e) { // NOSONAR
      Log.d("ERROR", "Exception: " + e.getMessage());
    }
  }

  boolean stop = false;
  String[] pipes = {"1Node","2Node"};

  @Background
  void pingRadioThread(PeripheralManagerService peripheralManagerService){
    try {

      //pingOut(peripheralManagerService);
     //pongBack(peripheralManagerService);

     // pongBackCallResponse(peripheralManagerService);
      //pingOutCallResponse(peripheralManagerService);
     dynPairPong(peripheralManagerService);
      //dynPairPing(peripheralManagerService);

    } catch (Exception e) { // NOSONAR
      Log.d("ERROR", "Exception: " + e.getMessage());
    }

  }

  private void pingOutCallResponse(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {
      radio.begin();
      radio.enableAckPayload();
      radio.enableDynamicPayloads();

      radio.openWritingPipe(pipes[0].getBytes());
      radio.openReadingPipe((byte) 1, pipes[1].getBytes());
      Log.d(TAG, radio.printDetails());

      byte counter = 1;

      radio.startListening();
      radio.writeAckPayload((byte) 1, new byte[]{counter}, 1);

      while (!stop) {
        radio.stopListening();
        Log.d(TAG, String.format("Now sending %d as payload.", counter));
        long time = SystemClock.uptimeMillis();
        if (radio.write(new byte[]{counter}, 1)) {
          if (!radio.available()) {
            Log.d(TAG, String.format("Got blank response. round trip delay: %d", SystemClock.uptimeMillis() - time));
          } else {
            while (radio.available()) {
              byte[] buffer = radio.read(1);
              Log.d(TAG, String.format("Got response %d. round-trip delay %d", buffer[0], SystemClock.uptimeMillis() - time));
              counter++;
            }
          }
        } else {
          Log.d(TAG, "Sending Failed.");
        }
        Thread.sleep(1000);
      }
    }

  }


  static final String send_payload_str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ789012";

  static final byte[][] dyn_pipes = {{(byte)0xE1,(byte)0xF0,(byte)0xF0,(byte)0xF0,(byte)0xF0}, {(byte)0xD2,(byte)0xF0,(byte)0xF0,(byte)0xF0,(byte)0xF0}};

  private void dynPairPong(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {
      radio.begin();
      radio.enableDynamicPayloads();
      radio.setRetries((byte) 5, (byte) 15);


      radio.openWritingPipe(dyn_pipes[1]);
      radio.openReadingPipe((byte) 1, dyn_pipes[0]);
      Log.d(TAG, radio.printDetails());
      radio.startListening();

      int nextPayloadSize = 4;

      byte[] send_payload = send_payload_str.getBytes();

      while (!stop) {

        if (radio.available()) {
          byte len = 0;
          byte[] receive_payload = {};
          while (radio.available()) {
            len = radio.getDynamicPayloadSize();
            receive_payload = radio.read(len);

            Log.d(TAG, String.format("Got payload size %d value %s", len, new String(receive_payload)));
          }
          radio.stopListening();
          if (len > 0) {
            radio.write(receive_payload, len);
            Log.d(TAG, "Sent Response");
          }
          radio.startListening();
        }


      }
    }
  }


  private void dynPairPing(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {

      radio.begin();
      radio.enableDynamicPayloads();
      radio.setRetries((byte) 5, (byte) 15);


      radio.openWritingPipe(dyn_pipes[0]);
      radio.openReadingPipe((byte) 1, dyn_pipes[1]);
      Log.d(TAG, radio.printDetails());

      int nextPayloadSize = 4;

      byte[] send_payload = send_payload_str.getBytes();

      while (!stop) {
        radio.stopListening();
        Log.d(TAG, String.format("Now Sending length %d", nextPayloadSize));

        radio.write(send_payload, nextPayloadSize);

        radio.startListening();

        long started_waiting_at = SystemClock.uptimeMillis();
        boolean timeout = false;
        while (!radio.available() && !timeout) {
          if (SystemClock.uptimeMillis() - started_waiting_at > 500) {
            timeout = true;
          }
        }

          if (timeout) {
            Log.d(TAG, "Failed, response timeout,");
          } else {
            byte len = radio.getDynamicPayloadSize();
            if (len > 0) {
              byte[] receive_payload = radio.read(len);
              // receive_payload[len] = 0;
              Log.d(TAG, String.format("got response size %d value=%s", len, new String(receive_payload)));
            } else {
              Log.d(TAG, "Dynamic payload size = 0");
            }
          }
          nextPayloadSize += 1;
          if (nextPayloadSize > 32) {
            nextPayloadSize = 4;
          }
          Thread.sleep(100);


      }
    }
  }

  private void pongBackCallResponse(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {

      radio.begin();
      radio.enableAckPayload();
      radio.enableDynamicPayloads();

      radio.openWritingPipe(pipes[0].getBytes());
      radio.openReadingPipe((byte) 1, pipes[1].getBytes());
      Log.d(TAG, radio.printDetails());

      byte counter = 1;

      radio.startListening();
      radio.writeAckPayload((byte) 1, new byte[]{counter}, 1);

      while (!stop) {
        byte pipeNo = radio.available(false);
        if (pipeNo > -1) {
          byte[] buffer = radio.read(1);
          buffer[0] += 1;
          radio.writeAckPayload(pipeNo, buffer, 1);
          Log.d(TAG, String.format("Loaded next response for pipe %d response %d", pipeNo, buffer[0]));
          Thread.sleep(900);
        } else {
          Log.d(TAG, "No available");
  //        Thread.sleep(1000);
        }
  //      radio.flushRx();
  //      radio.flushTx();

      }
    }

  }

  private void pongBack(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {

      radio.begin();

      radio.setRetries((byte) 15, (byte) 15);

      radio.openWritingPipe(pipes[0].getBytes());
      radio.openReadingPipe((byte) 1, pipes[1].getBytes());
      Log.d(TAG, radio.printDetails());

      radio.startListening();
      while (!stop) {
        if (radio.available()) {
          long got_time = 0;
          while (radio.available()) {
            byte[] got_buffer = radio.read(4);
            got_time = byteArrayToClong(got_buffer);
          }
          radio.stopListening();
          radio.write(longToCByteArray(got_time), 4);

          radio.startListening();

          Log.d(TAG, String.format("Got payload %d...", got_time));

          Thread.sleep(925);
        }
        //stop = true;
      }
    }
  }

  private void pingOut(PeripheralManagerService peripheralManagerService) throws IOException, InterruptedException {
    try (RF24 radio = new RF24(peripheralManagerService, cePin)) {

      radio.begin();

      radio.setRetries((byte) 15, (byte) 15);

      radio.openWritingPipe(pipes[0].getBytes());
      radio.openReadingPipe((byte) 1, pipes[1].getBytes());

      Log.d(TAG, radio.printDetails());

      radio.startListening();
      while (!stop) {
        radio.stopListening();
        Log.d(TAG, "Sending...");
        long time = SystemClock.uptimeMillis();
        byte[] buffer = longToCByteArray(time);
//            Longs.toByteArray(time);
//        for(int x = 0; x < 4; x++){
//          buffer[x] = buffer[x+4];
//        }
        boolean ok = radio.write(buffer, 4);

        if (!ok) {
          Log.e(TAG, "Send Failed");
        }
        radio.startListening();
        long started_waiting_at = SystemClock.uptimeMillis();
        boolean timeout = false;
        while (!radio.available() && !timeout) {
          if (SystemClock.uptimeMillis() - started_waiting_at > 200) {
            timeout = true;
          }
        }

        if (timeout) {
          Log.e(TAG, "Response timed out");
        } else {
          byte[] got_buffer = radio.read(4);
          long got_time = byteArrayToClong(got_buffer);

          Log.d(TAG, String.format("GOT Response %d, sent %d, trip delay %d", got_time, time, SystemClock.uptimeMillis() - time));
        }

        Thread.sleep(1000);

      }
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
        ((PCA9685Servo)deviceInterface).setServoAngle(usingChannel, getChannelLeftAngle(usingChannel));
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
        ((PCA9685Servo)deviceInterface).setServoAngle(usingChannel, getChannelRightAngle(usingChannel));
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
      rangeBar.setRangePinsByIndices(getChannelLeftAngle(usingChannel),getChannelRightAngle(usingChannel));
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

    if(ledPinRed != null) {
      ledPinRed.setValue(!ledPinRed.getValue());
    }
  }

  private void swapChannel(int channel) throws IOException {
    IODeviceInterface deviceInterface = DeviceHolder.getInstance().getDevice(DeviceHolder.Devices.PCA9685SERVO);
    if(deviceInterface != null && deviceInterface instanceof PCA9685Servo) {
      if(servoPositions[channel] == ServoPosition.LEFT) {
        ((PCA9685Servo)deviceInterface).setServoAngle(channel, getChannelRightAngle(channel));
        servoPositions[channel] = ServoPosition.RIGHT;
        lcdDriver.lcdClear();
        lcdDriver.lcdPuts("CH " + channel + " RIGHT");
      }else{
        ((PCA9685Servo)deviceInterface).setServoAngle(channel, getChannelLeftAngle(channel));
        servoPositions[channel] = ServoPosition.LEFT;
        lcdDriver.lcdClear();
        lcdDriver.lcdPuts("CH " + channel + " LEFT");
      }
    }
  }

  private void setChannelLeftAngle(int channel, int angle){
    channelLeftAngles.set(channel,angle);
    appPrefs.channelAnglesLeft().put(Joiner.on('|').join(channelLeftAngles));
  }

  private void setChannelRightAngle(int channel, int angle){
    channelRightAngles.set(channel,angle);
    appPrefs.channelAnglesRight().put(Joiner.on('|').join(channelRightAngles));
  }

  int getChannelLeftAngle(int channel){
    return channelLeftAngles.get(channel);
  }

  int getChannelRightAngle(int channel){
    return channelRightAngles.get(channel);
  }

  public native byte[] longToCByteArray(long value);
  public native long  byteArrayToClong(byte[] array);
}

package com.example.androidthings.pca6895servotest;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */
public class DeviceHolder {
  private static DeviceHolder ourInstance = new DeviceHolder();

  private MCP23017 deviceMCP23017;
  private PCA9685Servo devicePCAServo;

  public static DeviceHolder getInstance() {
    return ourInstance;
  }

  private DeviceHolder() {
  }

  public MCP23017 getDeviceMCP23017() {
    return deviceMCP23017;
  }

  public void setDeviceMCP23017(MCP23017 deviceMCP23017) {
    this.deviceMCP23017 = deviceMCP23017;
  }

  public PCA9685Servo getDevicePCAServo() {
    return devicePCAServo;
  }

  public void setDevicePCAServo(PCA9685Servo devicePCAServo) {
    this.devicePCAServo = devicePCAServo;
  }
}

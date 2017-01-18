package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */
@SuppressWarnings("WeakerAccess")
public class DeviceHolder {
  private static DeviceHolder ourInstance = new DeviceHolder();

  public enum Devices{
    MCP23017,
    PCA9685,
    PCA9685SERVO
  }

  private Map<Devices,IODeviceInterface> devices = new EnumMap<>(Devices.class);

  private DeviceHolder() {
  }

  public static DeviceHolder getInstance() {
    return ourInstance;
  }

  @Nullable
  public IODeviceInterface getDevice(@NonNull Devices device) {
    return devices.get(device);
  }

  public void setDevice(@NonNull Devices device, IODeviceInterface deviceInterface) {
    devices.put(device, deviceInterface);
  }

}

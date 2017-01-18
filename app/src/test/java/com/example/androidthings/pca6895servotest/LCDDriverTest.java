package com.example.androidthings.pca6895servotest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;

/**
 * Created by mathew on 18/01/17.
 * Copyright 2017 Mathew Winters
 */
@RunWith(MockitoJUnitRunner.class)
public class LCDDriverTest {
  static final int AF_E =   13;
  static final int AF_RW =   14;
  static final int AF_RS =   15;

  static final int AF_DB4 =   12;
  static final int AF_DB5 =   11;
  static final int AF_DB6 =   10;
  static final int AF_DB7 =  9;

  @Mock
  IODeviceInterface ioDeviceMock;

  LCDDriver lcdDriver;

  @Before
  public void setUp() throws Exception {
    InOrder inOrder = inOrder(ioDeviceMock);

    lcdDriver = new LCDDriver(ioDeviceMock,2,16,4,AF_RS,AF_E,AF_DB4,AF_DB5,AF_DB6,AF_DB7,0,0,0,0);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).setPinMode(AF_RS, IODeviceInterface.PinMode.MODE_OUTPUT);

    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).setPinMode(AF_E, IODeviceInterface.PinMode.MODE_OUTPUT);

    inOrder.verify(ioDeviceMock).setPinMode(AF_DB4, IODeviceInterface.PinMode.MODE_OUTPUT);
    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).setPinMode(AF_DB5, IODeviceInterface.PinMode.MODE_OUTPUT);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).setPinMode(AF_DB6, IODeviceInterface.PinMode.MODE_OUTPUT);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).setPinMode(AF_DB7, IODeviceInterface.PinMode.MODE_OUTPUT);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);

    // 8 BIT MODE 3 TIMES
    for(int x = 0; x < 3; x++) {
      inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);
      inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
      inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
      inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
      inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
      // strobe
      inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
      inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);
    }

    // 4 BIT MODE
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lCD Lines 8 bits of command.
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
     // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lcdCursor

    // lcdCursorBlink

    // lcdClear

//      inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void lcdHome() throws Exception {

  }

  @Test
  public void lcdClear() throws Exception {

  }

  @Test
  public void lcdDisplay() throws Exception {

  }

  @Test
  public void lcdCursor() throws Exception {

  }

  @Test
  public void lcdCursorBlink() throws Exception {

  }

  @Test
  public void lcdSendCommand() throws Exception {

  }

  @Test
  public void lcdPosition() throws Exception {

  }

  @Test
  public void lcdCharDef() throws Exception {

  }

  @Test
  public void lcdPutchar() throws Exception {

  }

  @Test
  public void lcdPuts() throws Exception {

  }

}
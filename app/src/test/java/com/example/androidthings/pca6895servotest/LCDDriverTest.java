package com.example.androidthings.pca6895servotest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

/**
 * Created by mathew on 18/01/17.
 * Copyright 2017 Mathew Winters
 */
@RunWith(MockitoJUnitRunner.class)
public class LCDDriverTest {
  private static final int AF_E = 13;
  private static final int AF_RS = 15;

  private static final int AF_DB4 = 12;
  private static final int AF_DB5 = 11;
  private static final int AF_DB6 = 10;
  private static final int AF_DB7 = 9;

  @Mock
  IODeviceInterface ioDeviceMock;

  private LCDDriver lcdDriver;

  //@Before
  public InOrder setUp() throws Exception {
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

      inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
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

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
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

    // lcdDisplay
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lcdCursor
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lcdCursorBlink
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lcdClear
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // LCD Home from LCD Clear
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    // LCD_ENTRY | LCD_ENTRY_ID
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // LCD_CDSHIFT | LCD_CDSHIFT_RL
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
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    return inOrder;
  }

  @Test
  public void construction() throws Exception {
    InOrder inOrder = setUp();
    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void lcdHome() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdHome");
    lcdDriver.lcdHome();

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void lcdClear() throws Exception {

    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdClear");
    lcdDriver.lcdClear();

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    // lcdhome is called from clear
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void lcdDisplay() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdDisplay true");
    lcdDriver.lcdDisplay(true);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //System.out.println("lcdDisplay false");
    lcdDriver.lcdDisplay(false);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
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

    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void lcdCursor() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdCursor true");
    lcdDriver.lcdCursor(true);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //System.out.println("lcdCursor false");
    lcdDriver.lcdCursor(false);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verifyNoMoreInteractions();


  }

  @Test
  public void lcdCursorBlink() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdCursorBlink true");
    lcdDriver.lcdCursorBlink(true);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //System.out.println("lcdCursorBlink false");
    lcdDriver.lcdCursorBlink(false);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);
    inOrder.verifyNoMoreInteractions();


  }

  @Test
  public void lcdPosition() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdPosition 1,1");
    lcdDriver.lcdPosition(1, 0);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    //System.out.println("lcdPosition 10,1");
    lcdDriver.lcdPosition(10, 1);

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void lcdCharDef() throws Exception {

  }

  @Test
  public void lcdPutchar() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdPutchar A");
    lcdDriver.lcdPutchar((byte) 'A');

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    //System.out.println("lcdPutchar !");
    lcdDriver.lcdPutchar((byte) '!');

    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void lcdPuts() throws Exception {
    InOrder inOrder = setUp();
    //System.out.println();
    //System.out.println("lcdPuts BOB");
    lcdDriver.lcdPuts("BOB");

    //B
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //O
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //B
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);


    //System.out.println("lcdPuts bob");
    lcdDriver.lcdPuts("bob");

    //b
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //o
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.HIGH);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    //b
    inOrder.verify(ioDeviceMock).writePin(AF_RS, IODeviceInterface.PinState.HIGH);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

    inOrder.verify(ioDeviceMock).writePin(AF_DB4, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB5, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_DB6, IODeviceInterface.PinState.LOW);
    inOrder.verify(ioDeviceMock).writePin(AF_DB7, IODeviceInterface.PinState.LOW);
    // strobe
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.HIGH);
    inOrder.verify(ioDeviceMock).writePin(AF_E, IODeviceInterface.PinState.LOW);

  }

}
package com.example.androidthings.pca6895servotest;

import android.support.annotation.NonNull;

import java.io.IOException;


/**
 * Created by mathew on 18/01/17.
 * Copyright 2017 Mathew Winters
 * <p>
 * Text-based LCD driver.
 * This is designed to drive the parallel interface LCD drivers
 * based in the Hitachi HD44780U controller and compatables.
 *
 * Original Code from:
 *  * Copyright (c) 2012 Gordon Henderson.
 ***********************************************************************
 * This file is part of wiringPi:
 *	https://projects.drogon.net/raspberry-pi/wiringpi/
 *
 *    wiringPi is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    wiringPi is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with wiringPi.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************
 */

@SuppressWarnings({"WeakerAccess", "unused", "squid:S00115", "squid:S1068"})
public class LCDDriver {
  // HD44780U Commands
  private static final int LCD_CLEAR = 0x01;
  private static final int LCD_HOME = 0x02;
  private static final int LCD_ENTRY = 0x04;
  private static final int LCD_CTRL = 0x08;
  private static final int LCD_CDSHIFT = 0x10;
  private static final int LCD_FUNC = 0x20;
  private static final int LCD_CGRAM = 0x40;
  private static final int LCD_DGRAM = 0x80;

  // Bits in the entry register
  private static final int LCD_ENTRY_SH = 0x01;
  private static final int LCD_ENTRY_ID = 0x02;

  // Bits in the control register
  private static final int LCD_BLINK_CTRL = 0x01;
  private static final int LCD_CURSOR_CTRL = 0x02;
  private static final int LCD_DISPLAY_CTRL = 0x04;

  // Bits in the function register
  private static final int LCD_FUNC_F = 0x04;
  private static final int LCD_FUNC_N = 0x08;
  private static final int LCD_FUNC_DL = 0x10;

  private static final int LCD_CDSHIFT_RL = 0x04;

  private static final int[] rowOff = {0x00, 0x40, 0x14, 0x54};

  private final IODeviceInterface ioDeviceInterface;

  private final int bits;
  private final int rows;
  private final int cols;
  private final int rsPin;
  private final int strbPin;
  private final int[] dataPins = new int[8];
  private int cx;
  private int cy;
  private int lcdControl;

  @SuppressWarnings("squid:S00107")
  public LCDDriver(@NonNull IODeviceInterface ioDeviceInterface,
                   int rows, int cols, int bits,
                   int rsPin, int strbPin,
                   int d0, int d1, int d2, int d3, int d4,
                   int d5, int d6, int d7) throws IOException {
    this.ioDeviceInterface = ioDeviceInterface;
    this.bits = bits;
    this.rows = rows;
    this.cols = cols;
    this.rsPin = rsPin;
    this.strbPin = strbPin;
    this.dataPins[0] = d0;
    this.dataPins[1] = d1;
    this.dataPins[2] = d2;
    this.dataPins[3] = d3;
    this.dataPins[4] = d4;
    this.dataPins[5] = d5;
    this.dataPins[6] = d6;
    this.dataPins[7] = d7;

    if (!((bits == 4) || (bits == 8)))
      throw new UnsupportedOperationException("Bits must be 4 or 8");

    if ((rows < 0) || (rows > 20))
      throw new UnsupportedOperationException("Rows must be between 0 and 20");

    if ((cols < 0) || (cols > 20))
      throw new UnsupportedOperationException("Columns must be between 0 and 20");

    digitalWrite(rsPin, 0);
    pinMode(rsPin, IODeviceInterface.PinMode.MODE_OUTPUT);
    digitalWrite(strbPin, 0);
    pinMode(strbPin, IODeviceInterface.PinMode.MODE_OUTPUT);

    for (int i = 0; i < bits; ++i) {
      pinMode(dataPins[i], IODeviceInterface.PinMode.MODE_OUTPUT);
      digitalWrite(dataPins[i], 0);
    }
    delay(35); // mS


// 4-bit mode?
//	OK. This is a PIG and it's not at all obvious from the documentation I had,
//	so I guess some others have worked through either with better documentation
//	or more trial and error... Anyway here goes:
//
//	It seems that the controller needs to see the FUNC command at least 3 times
//	consecutively - in 8-bit mode. If you're only using 8-bit mode, then it appears
//	that you can get away with one func-set, however I'd not rely on it...
//
//	So to set 4-bit mode, you need to send the commands one nibble at a time,
//	the same three times, but send the command to set it into 8-bit mode those
//	three times, then send a final 4th command to set it into 4-bit mode, and only
//	then can you flip the switch for the rest of the library to work in 4-bit
//	mode which sends the commands as 2 x 4-bit values.
    int func;
    if (bits == 4) {
      func = LCD_FUNC | LCD_FUNC_DL;      // Set 8-bit mode 3 times
      put4Command(func >> 4);
      delay(35);
      put4Command(func >> 4);
      delay(35);
      put4Command(func >> 4);
      delay(35);
      func = LCD_FUNC;          // 4th set: 4-bit mode
      put4Command(func >> 4);
      delay(35);
    } else {
      func = LCD_FUNC | LCD_FUNC_DL;
      putCommand(func);
      delay(35);
      putCommand(func);
      delay(35);
      putCommand(func);
      delay(35);
    }

    if (rows > 1) {
      func |= LCD_FUNC_N;
      putCommand(func);
      delay(35);
    }

// Rest of the initialisation sequence

    lcdDisplay(true);
    lcdCursor(false);
    lcdCursorBlink(false);
    lcdClear();

    putCommand(LCD_ENTRY | LCD_ENTRY_ID);
    putCommand(LCD_CDSHIFT | LCD_CDSHIFT_RL);


  }

  private void pinMode(int pin, IODeviceInterface.PinMode mode) throws IOException {
    ioDeviceInterface.setPinMode(pin, mode);
  }

  private void digitalWrite(int pin, int value) throws IOException {
    //System.out.println("digitalWrite " + pin + " " + value); // NOSONAR
    ioDeviceInterface.writePin(pin, value == 1 ? IODeviceInterface.PinState.HIGH : IODeviceInterface.PinState.LOW);
  }

  private void delay(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

  }

  private void delayMicroseconds(int microseconds) {
    try {
      Thread.sleep(0, microseconds * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /*
 * strobe:
 *	Toggle the strobe (Really the "E") pin to the device.
 *	According to the docs, data is latched on the falling edge.
 *********************************************************************************
 */
  private void strobe() throws IOException {
// Note timing changes for new version of delayMicroseconds ()
    digitalWrite(strbPin, 1);
    delayMicroseconds(50);
    digitalWrite(strbPin, 0);
    delayMicroseconds(50);
  }


  /*
   * sentDataCmd:
   *	Send an data or command byte to the display.
   *********************************************************************************
   */
  private void sendDataCmd(byte data) throws IOException {
    byte myData = data;
    byte i;
    byte d4;

    if (bits == 4) {
      d4 = (byte) ((myData >> 4) & 0x0F);
      for (i = 0; i < 4; ++i) {
        digitalWrite(dataPins[i], d4 & 1);
        d4 >>= 1;
      }
      strobe();

      d4 = (byte) (myData & 0x0F);
      for (i = 0; i < 4; ++i) {
        digitalWrite(dataPins[i], d4 & 1);
        d4 >>= 1;
      }
    } else {
      for (i = 0; i < 8; ++i) {
        digitalWrite(dataPins[i], myData & 1);
        myData >>= 1;
      }
    }
    strobe();
  }

  /*
 * putCommand:
 *	Send a command byte to the display
 *********************************************************************************
 */

  private void putCommand(int command) throws IOException {
    digitalWrite(rsPin, 0);
    sendDataCmd((byte) command);
    delay(2);
  }

  private void put4Command(int command) throws IOException {
    byte myCommand = (byte) command;
    byte i;

    digitalWrite(rsPin, 0);

    for (i = 0; i < 4; ++i) {
      digitalWrite(dataPins[i], myCommand & 1);
      myCommand >>= 1;
    }
    strobe();
  }

  /*
 * lcdHome: lcdClear:
 *	Home the cursor or clear the screen.
 *********************************************************************************
 */

  void lcdHome() throws IOException {
    putCommand(LCD_HOME);
    cx = 0;
    cy = 0;
    delay(5);
  }

  void lcdClear() throws IOException {
    putCommand(LCD_CLEAR);
    putCommand(LCD_HOME);
    cx = 0;
    cy = 0;
    delay(5);
  }

  /*
 * lcdDisplay: lcdCursor: lcdCursorBlink:
 *	Turn the display, cursor, cursor blinking on/off
 *********************************************************************************
 */

  void lcdDisplay(boolean state) throws IOException {
    if (state)
      lcdControl |= LCD_DISPLAY_CTRL;
    else
      lcdControl &= ~LCD_DISPLAY_CTRL;

    putCommand(LCD_CTRL | lcdControl);
  }

  void lcdCursor(boolean state) throws IOException {
    if (state)
      lcdControl |= LCD_CURSOR_CTRL;
    else
      lcdControl &= ~LCD_CURSOR_CTRL;

    putCommand(LCD_CTRL | lcdControl);
  }

  void lcdCursorBlink(boolean state) throws IOException {
    if (state)
      lcdControl |= LCD_BLINK_CTRL;
    else
      lcdControl &= ~LCD_BLINK_CTRL;

    putCommand(LCD_CTRL | lcdControl);
  }

  /*
   * lcdSendCommand:
   *	Send any arbitary command to the display
   *********************************************************************************
   */
  public void lcdSendCommand(byte command) throws IOException {
    putCommand(command);
  }


/*
 * lcdPosition:
 *	Update the position of the cursor on the display.
 *	Ignore invalid locations.
 *********************************************************************************
 */

  public void lcdPosition(int x, int y) throws IOException {
    if ((x >= cols) || (x < 0)) {
      return;
    }
    if ((y >= rows) || (y < 0)) {
      return;
    }

    putCommand(x + (LCD_DGRAM | rowOff[y]));

    cx = x;
    cy = y;
  }

/*
 * lcdCharDef:
 *	Defines a new character in the CGRAM
 *********************************************************************************
 */

  public void lcdCharDef(int index, byte[] data) throws IOException {
    putCommand(LCD_CGRAM | ((index & 7) << 3));

    digitalWrite(rsPin, 1);
    for (int i = 0; i < 8; ++i) {
      sendDataCmd(data[i]);
    }
  }

/*
 * lcdPutchar:
 *	Send a data byte to be displayed on the display. We implement a very
 *	simple terminal here - with line wrapping, but no scrolling. Yet.
 *********************************************************************************
 */

  void lcdPutchar(byte data) throws IOException {
    digitalWrite(rsPin, 1);
    sendDataCmd(data);

    if (++cx == cols) {
      cx = 0;
      if (++cy == rows) {
        cy = 0;
      }

      putCommand(cx + (LCD_DGRAM | rowOff[cy]));
    }
  }

/*
 * lcdPuts:
 *	Send a string to be displayed on the display
 *********************************************************************************
 */

  public void lcdPuts(String string) throws IOException {
    for (byte ch : string.getBytes()) {
      lcdPutchar(ch);
    }
  }

}

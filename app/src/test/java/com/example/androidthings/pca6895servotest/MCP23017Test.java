package com.example.androidthings.pca6895servotest;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author MWinters
 */
@SuppressWarnings({"WeakerAccess", "unused","squid:S00115", "squid:S1068"})
@RunWith(MockitoJUnitRunner.class)
public class MCP23017Test {
  private static final int MCP23x17_IODIRA = 0x00;
  private static final int MCP23x17_IPOLA = 0x02;
  private static final int MCP23x17_GPINTENA = 0x04;
  private static final int MCP23x17_DEFVALA = 0x06;
  private static final int MCP23x17_INTCONA = 0x08;
  private static final int MCP23x17_IOCON = 0x0A;
  private static final int MCP23x17_GPPUA = 0x0C;
  private static final int MCP23x17_INTFA = 0x0E;
  private static final int MCP23x17_INTCAPA = 0x10;
  private static final int MCP23x17_GPIOA = 0x12;
  private static final int MCP23x17_OLATA = 0x14;
  private static final int MCP23x17_IODIRB = 0x01;
  private static final int MCP23x17_IPOLB = 0x03;
  private static final int MCP23x17_GPINTENB = 0x05;
  private static final int MCP23x17_DEFVALB = 0x07;
  private static final int MCP23x17_INTCONB = 0x09;
  private static final int MCP23x17_IOCONB = 0x0B;
  private static final int MCP23x17_GPPUB = 0x0D;
  private static final int MCP23x17_INTFB = 0x0F;
  private static final int MCP23x17_INTCAPB = 0x11;
  private static final int MCP23x17_GPIOB = 0x13;

  // Bits in the IOCON register
  private static final int MCP23x17_OLATB = 0x15;
  private static final int IOCON_UNUSED = 0x01;
  private static final int IOCON_INTPOL = 0x02;
  private static final int IOCON_ODR = 0x04;
  private static final int IOCON_HAEN = 0x08;
  private static final int IOCON_DISSLW = 0x10;
  private static final int IOCON_SEQOP = 0x20;
  private static final int IOCON_MIRROR = 0x40;
  private static final int IOCON_INIT = IOCON_SEQOP;


  @Mock
  public PeripheralManagerService peripheralManagerServiceMock;

  @Mock
  public I2cDevice i2cDeviceMock;

  private List<String> i2cDevices = new ArrayList<>();

  @Before
  public void setUp() throws Exception {

    i2cDevices.add("I2C1");

  }


  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void setPinMode() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x20)).thenReturn(i2cDeviceMock);

    // init..
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATA)).thenReturn((byte)0);
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATB)).thenReturn((byte)0);

    InOrder inOrder = inOrder(i2cDeviceMock);

    MCP23017 device = new MCP23017((byte)0x20, peripheralManagerServiceMock);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IOCON, (byte) IOCON_INIT);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATA);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATB);

    when(i2cDeviceMock.readRegByte(MCP23x17_IODIRA)).thenReturn((byte)0b11111111).thenReturn((byte)0b11111101);
    when(i2cDeviceMock.readRegByte(MCP23x17_IODIRB)).thenReturn((byte)0b00000000);

    device.setPinMode(1, MCP23017.PinMode.MODE_OUTPUT);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IODIRA, (byte)0b11111101);

    when(i2cDeviceMock.readRegByte(MCP23x17_GPPUA)).thenReturn((byte)0b00000000);
    device.setPinMode(1, MCP23017.PinMode.MODE_INPUT);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IODIRA, (byte)0b11111111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPPUA, (byte)0b00000000);

    when(i2cDeviceMock.readRegByte(MCP23x17_GPPUB)).thenReturn((byte)0b00000000);
    device.setPinMode(8, MCP23017.PinMode.MODE_INPUT_PULLUP);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IODIRB, (byte)0b00000001);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPPUB, (byte)0b00000001);

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void readPin() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x20)).thenReturn(i2cDeviceMock);

    InOrder inOrder = inOrder(i2cDeviceMock);

    // init..
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATA)).thenReturn((byte)0);
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATB)).thenReturn((byte)0);

    MCP23017 device = new MCP23017((byte)0x20, peripheralManagerServiceMock);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IOCON, (byte) IOCON_INIT);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATA);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATB);

    when(i2cDeviceMock.readRegByte(MCP23x17_GPIOA)).thenReturn((byte)0b10101010);
    when(i2cDeviceMock.readRegByte(MCP23x17_GPIOB)).thenReturn((byte)0b01010101);

    assertEquals(device.readPin(0), MCP23017.PinState.LOW);
    assertEquals(device.readPin(1), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(2), MCP23017.PinState.LOW);
    assertEquals(device.readPin(3), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(4), MCP23017.PinState.LOW);
    assertEquals(device.readPin(5), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(6), MCP23017.PinState.LOW);
    assertEquals(device.readPin(7), MCP23017.PinState.HIGH);

    inOrder.verify(i2cDeviceMock,times(8)).readRegByte(MCP23x17_GPIOA);

    assertEquals(device.readPin(8), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(9), MCP23017.PinState.LOW);
    assertEquals(device.readPin(10), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(11), MCP23017.PinState.LOW);
    assertEquals(device.readPin(12), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(13), MCP23017.PinState.LOW);
    assertEquals(device.readPin(14), MCP23017.PinState.HIGH);
    assertEquals(device.readPin(15), MCP23017.PinState.LOW);

    inOrder.verify(i2cDeviceMock,times(8)).readRegByte(MCP23x17_GPIOB);

    inOrder.verifyNoMoreInteractions();

  }

  @Test
  public void writePin() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x20)).thenReturn(i2cDeviceMock);

    // init..
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATA)).thenReturn((byte)0);
    when(i2cDeviceMock.readRegByte(MCP23x17_OLATB)).thenReturn((byte)0);

    InOrder inOrder = inOrder(i2cDeviceMock);

    MCP23017 device = new MCP23017((byte)0x20, peripheralManagerServiceMock);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_IOCON, (byte) IOCON_INIT);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATA);
    inOrder.verify(i2cDeviceMock).readRegByte(MCP23x17_OLATB);


    device.writePin(0, MCP23017.PinState.HIGH);
    device.writePin(1, MCP23017.PinState.HIGH);
    device.writePin(2, MCP23017.PinState.HIGH);
    device.writePin(3, MCP23017.PinState.HIGH);
    device.writePin(4, MCP23017.PinState.HIGH);
    device.writePin(5, MCP23017.PinState.HIGH);
    device.writePin(6, MCP23017.PinState.HIGH);
    device.writePin(7, MCP23017.PinState.HIGH);

    device.writePin(0, MCP23017.PinState.LOW);
    device.writePin(1, MCP23017.PinState.LOW);
    device.writePin(2, MCP23017.PinState.LOW);
    device.writePin(3, MCP23017.PinState.LOW);
    device.writePin(4, MCP23017.PinState.LOW);
    device.writePin(5, MCP23017.PinState.LOW);
    device.writePin(6, MCP23017.PinState.LOW);
    device.writePin(7, MCP23017.PinState.LOW);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00000001);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00000011);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00000111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00001111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00011111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00111111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b01111111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11111111);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11111110);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11111100);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11111000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11110000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11100000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b11000000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b10000000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOA,(byte)0b00000000);


    device.writePin(8,  MCP23017.PinState.HIGH);
    device.writePin(9,  MCP23017.PinState.HIGH);
    device.writePin(10, MCP23017.PinState.HIGH);
    device.writePin(11, MCP23017.PinState.HIGH);
    device.writePin(12, MCP23017.PinState.HIGH);
    device.writePin(13, MCP23017.PinState.HIGH);
    device.writePin(14, MCP23017.PinState.HIGH);
    device.writePin(15, MCP23017.PinState.HIGH);

    device.writePin(8,  MCP23017.PinState.LOW);
    device.writePin(9,  MCP23017.PinState.LOW);
    device.writePin(10, MCP23017.PinState.LOW);
    device.writePin(11, MCP23017.PinState.LOW);
    device.writePin(12, MCP23017.PinState.LOW);
    device.writePin(13, MCP23017.PinState.LOW);
    device.writePin(14, MCP23017.PinState.LOW);
    device.writePin(15, MCP23017.PinState.LOW);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00000001);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00000011);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00000111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00001111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00011111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00111111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b01111111);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11111111);

    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11111110);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11111100);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11111000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11110000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11100000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b11000000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b10000000);
    inOrder.verify(i2cDeviceMock).writeRegByte(MCP23x17_GPIOB,(byte)0b00000000);


    inOrder.verifyNoMoreInteractions();
  }

}
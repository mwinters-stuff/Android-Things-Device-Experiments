package pca6895servotest;


import android.support.test.filters.MediumTest;

import com.example.androidthings.pca6895servotest.PCA9685Servo;
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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * @author MWinters
 */
@RunWith(MockitoJUnitRunner.class)
@MediumTest
public class PCA9685ServoTest {
  private static final int MODE1 = 0x00;
  private static final int MODE2 = 0x01;
  //  private static final int SUBADR1 = 0x02; // NOSONAR
//  private static final int SUBADR2 = 0x03; // NOSONAR
//  private static final int SUBADR3 = 0x04; // NOSONAR
  private static final int PRESCALE = 0xFE;
  private static final int LED0_ON_L = 0x06;
  private static final int LED0_ON_H = 0x07;
  private static final int LED0_OFF_L = 0x08;
  private static final int LED0_OFF_H = 0x09;
  private static final int ALL_LED_ON_L = 0xFA;
  private static final int ALL_LED_ON_H = 0xFB;
  private static final int ALL_LED_OFF_L = 0xFC;
  private static final int ALL_LED_OFF_H = 0xFD;
  // Bits
//  private static final int RESTART = 0x80; // NOSONAR
  private static final int SLEEP = 0x10;
  private static final int ALLCALL = 0x01;
  //  private static final int INVRT = 0x10; // NOSONAR
  private static final int OUTDRV = 0x04;

  private PCA9685Servo pca9685Servo;
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
  public void creation() throws Exception {
    InOrder inOrder = inOrder(i2cDeviceMock);

    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685Servo pca9685Servo = new PCA9685Servo((byte)0x40, peripheralManagerServiceMock);

    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_L, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_H, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_L, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_H, (byte) (0));

    inOrder.verify(i2cDeviceMock).writeRegByte(MODE2, (byte) OUTDRV);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) ALLCALL);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) (0));

    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte)16); //#go to sleep
    inOrder.verify(i2cDeviceMock).writeRegByte(PRESCALE, (byte)121);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) 0);

    pca9685Servo.setPwm(1,10,0);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4, (byte) (10 & 0xFF));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4, (byte) (10 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4, (byte) (0));

  }


  @Test
  public void setServoAngle() throws Exception {
    creation();
    pca9685Servo.setServoMinMaxPwm(0,180,10,210);

    pca9685Servo.setServoAngle(2,90);
    InOrder inOrder = inOrder(i2cDeviceMock);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4, (byte) (10 & 0xFF));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4, (byte) (10 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4, (byte) (0));

  }

  @Test
  public void setPwmFreq() throws Exception {

  }

  @Test
  public void setPwm() throws Exception {

  }

  @Test
  public void setAllPwm() throws Exception {

  }

  @Test
  public void map() throws Exception {

  }

  @Test
  public void getCurrentPwm() throws Exception {

  }

}
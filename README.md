[![Build Status](https://travis-ci.org/wintersandroid/Android-Things-Device-Experiments.svg?branch=master)](https://travis-ci.org/wintersandroid/Android-Things-Device-Experiments)

Android Things Device Experiments
=====================================

This is my play with Android Things and a couple of devices I have to make a Lego Train Points switch
with a servo.

This works well.


Pre-requisites
--------------

- Raspberry Pi 3
- Android Studio 2.2+
- PCA9685 PWM Board
- Adafruit RGB LCD (Negative backlight)
- Servo Motors
- Screen hooked up to HDMI and Mouse.

Build and install
=================

On Android Studio, click on the "Run" button.

Use
===
Look at the display, you get a range bar to select 0-180 degrees for left and right.
My lego points and servo seem to be good on 20 and 70 Degrees.
Select the channel
Click the buttons to test.

You can also use the buttons on the LCD board to select channels 0-4 with the
4 cursor buttons. The "Select" button will flip through different backlight
colours.

License
-------

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.

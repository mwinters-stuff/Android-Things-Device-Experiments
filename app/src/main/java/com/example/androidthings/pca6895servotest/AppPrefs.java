package com.example.androidthings.pca6895servotest;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface AppPrefs {
  @DefaultInt(0)
  int angleLeft();

  @DefaultInt(0)
  int angleRight();

  @DefaultInt(0)
  int selectedChannel();

}


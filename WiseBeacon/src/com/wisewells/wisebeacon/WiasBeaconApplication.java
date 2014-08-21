package com.wisewells.wisebeacon;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Application;

public class WiasBeaconApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CalligraphyConfig.initDefault("fonts/nanum_regular.ttf", R.attr.fontPath);
	}
}

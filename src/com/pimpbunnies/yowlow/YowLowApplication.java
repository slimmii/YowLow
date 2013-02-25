package com.pimpbunnies.yowlow;

import android.app.Application;

public class YowLowApplication extends Application {
	private static YowLowApplication singleton;

	public static YowLowApplication getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
	}
}

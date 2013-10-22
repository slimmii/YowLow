package com.pimpbunnies.yowlow;

import android.app.Application;

public class YowLowApplication extends Application {
	private static YowLowApplication singleton;

	private boolean deleting;
	
	public static YowLowApplication getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
	}

	public boolean toggleDelete() {
		deleting = !deleting;
		return deleting;
	}

	public boolean isDeleting() {
		return deleting;
	}
	
	
}

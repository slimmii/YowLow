package com.pimpbunnies.yowlow;

import com.pimpbunnies.yowlow.views.GenericDieView;

import android.graphics.drawable.Drawable;

public class DiceKind {
	private String mName;
	private String mDescription;
	private Drawable mIcon;
	

	public DiceKind(String name, String description, Drawable icon) {
		super();
		this.mName = name;
		this.mDescription = description;
		this.mIcon = icon;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		this.mDescription = description;
	}
	public Drawable getIcon() {
		return mIcon;
	}
	public void setIcon(Drawable icon) {
		this.mIcon = icon;
	}
	
	
	
}

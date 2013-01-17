package com.pimpbunnies.yowlow;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class GenericDieView extends ImageView {

	public GenericDieView(Context context) {
		super(context);
		this.setPadding(10, 10, 10, 10);
		android.support.v7.widget.GridLayout.LayoutParams params = new android.support.v7.widget.GridLayout.LayoutParams();
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);
		this.setBackgroundResource(R.drawable.image_stroke);
		this.setLayoutParams(params);

	}
	
	public abstract void shuffle();

}

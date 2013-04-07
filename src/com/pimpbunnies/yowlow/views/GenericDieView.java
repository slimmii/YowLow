package com.pimpbunnies.yowlow.views;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.threedee.Cube;
import com.pimpbunnies.yowlow.threedee.GLView;

public abstract class GenericDieView<T> extends LinearLayout implements OnTouchListener {
	protected GLView mView;
	
	protected Cube mCube;
	
	private Context mContext;
	
	public GenericDieView(Context context) {
		super(context);
		mContext = context;
		mCube = new Cube(getDefaultFaces());		
		mView = new GLView(mContext, mCube);
		
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		
		android.support.v7.widget.GridLayout.LayoutParams params = new android.support.v7.widget.GridLayout.LayoutParams();
		params.width = (int) width / 3;
		params.height = (int) width / 3;
		
		
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mView.setLayoutParams(params);
		
		mView.setOnTouchListener(this);
	
		this.addView(mView);		
	}
	
	public Bitmap[] getDefaultFaces() {
		Bitmap[] bitmaps = new Bitmap[6];
		for (int i=0; i < 6 ; i++) {
			bitmaps[i] = BitmapFactory.decodeResource(getResources(), R.drawable.grumpy);		
		}
		
		return bitmaps;
	}

	public abstract Bitmap[] getFaces();
		
	public T shuffle() {
		return shuffle(new ShuffleCallback() {
			@Override
			public void shuffled() {
			}
		});
	}
		
	public abstract T shuffle(final ShuffleCallback cb);
	
	@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
		shuffle();
 		return false;
    }
}


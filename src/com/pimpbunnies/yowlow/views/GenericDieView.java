package com.pimpbunnies.yowlow.views;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.threedee.Cube;
import com.pimpbunnies.yowlow.threedee.GLView;

public abstract class GenericDieView<T> extends LinearLayout implements OnTouchListener {
	protected GLView mView;
	
	protected Cube mCube;
	
	private Context mContext;
	
	public GenericDieView(Context context) {
		super(context);
		mContext = context;
		mCube = new Cube(getFaces());		
		mView = new GLView(mContext, mCube);
		
		android.support.v7.widget.GridLayout.LayoutParams params = new android.support.v7.widget.GridLayout.LayoutParams();
		params.width = 150;
		params.height = 150;
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mView.setLayoutParams(params);
		
		mView.setOnTouchListener(this);
	
		this.addView(mView);		
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


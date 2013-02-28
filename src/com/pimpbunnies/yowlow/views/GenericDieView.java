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
import com.pimpbunnies.yowlow.threedee.GLView;

public abstract class GenericDieView<T> extends LinearLayout implements OnTouchListener {
	protected GLView mView;
	private Context mContext;
	
	public GenericDieView(Context context) {
		super(context);
		mContext = context;
	}
	
	public void setTextures(Bitmap[] bitmaps) {
		this.removeView(mView);
		mView = new GLView(mContext, bitmaps);
		
		android.support.v7.widget.GridLayout.LayoutParams params = new android.support.v7.widget.GridLayout.LayoutParams();
		params.width = 150;
		params.height = 150;
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mView.setLayoutParams(params);
		
		mView.setOnTouchListener(this);
	
		this.addView(mView);
	}
	
	public void reset() {
		Bitmap grumpy = BitmapFactory.decodeResource(getResources(), R.drawable.grumpy);
		
		Bitmap bitmap[] = new Bitmap[] {
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				BitmapFactory.decodeResource(getResources(), R.drawable.grumpy),
				
		};

		setTextures(bitmap);
	}
	
	public T shuffle() {
		return shuffle(new ShuffleCallback() {
			@Override
			public void shuffled() {
			}
		});
	}
	
	public T shuffle(ArrayList<T> ignore) {
		return shuffle(ignore, new ShuffleCallback() {
			@Override
			public void shuffled() {
			}
		});
	}
	
	public abstract T shuffle(ArrayList<T> ignore, final ShuffleCallback cb);
	public abstract T shuffle(final ShuffleCallback cb);
	
	@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
		shuffle();
 		return false;
    }
}


package com.pimpbunnies.yowlow.views;
import java.util.ArrayList;

import com.pimpbunnies.yowlow.R;

import android.content.ClipData;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

public abstract class GenericDieView<T> extends ImageView implements OnTouchListener {

	private final static int START_DRAGGING = 0;
	private final static int STOP_DRAGGING = 1;

	private int status;
	
	public GenericDieView(Context context) {
		super(context);
		this.setPadding(10, 10, 10, 10);
		android.support.v7.widget.GridLayout.LayoutParams params = new android.support.v7.widget.GridLayout.LayoutParams();
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);
		this.setLayoutParams(params);
		this.setBackgroundResource(R.drawable.image_stroke_generic_die);
		
		this.setOnTouchListener(this);
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
		return false;
    }
}

package com.pimpbunnies.yowlow.views;

import java.util.ArrayList;
import java.util.Random;

import com.pimpbunnies.yowlow.model.Guest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

public class RealDieView extends GenericDieView<Integer> {
	
	private int fCounter = 0;
	private int fCurrentNumber = 6;
	private boolean fShuffleing = false;
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			shuffle();
		}
	};
	
	
	public RealDieView(Context context) {
		super(context);
		Bitmap whiteBitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
		this.setImageBitmap(whiteBitmap);
		this.setOnClickListener(onClickListener);
	}

	@Override
	public Integer shuffle(final ShuffleCallback cb) {
		return shuffle(new ArrayList<Integer>(), cb);
	}
	
	@Override
	public Integer shuffle(ArrayList<Integer> ignore, final ShuffleCallback cb) {
		final Handler handler = new Handler();
		final Random rand = new Random();
		fShuffleing = true;
		handler.postDelayed(new Runnable() {
			public void run() {
				fCurrentNumber = 1 + rand.nextInt(6);
				refreshDrawableState();
				invalidate();
				
				//fGuestName.setText(guest.getName());
				if (fCounter++ >= 20) {
					fCounter = 0;
					fShuffleing = false;
					
					cb.shuffled();
				} else {
					handler.postDelayed(this, 200);
				}
			}
		}, 0);
		return 0;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		System.out.println(">>>>>>>>>>>>>>>>>>>>" + canvas.getWidth());
		switch (fCurrentNumber) {
		case 1: canvas.drawCircle(65, 65, 10, paint);
				break;
		case 2: canvas.drawCircle(40, 40, 10, paint);
				canvas.drawCircle(90, 90, 10, paint);
				break;
		case 3: canvas.drawCircle(40, 40, 10, paint);
				canvas.drawCircle(65, 65, 10, paint);
				canvas.drawCircle(90, 90, 10, paint);
				break;
		case 4: canvas.drawCircle(40, 40, 10, paint);
				canvas.drawCircle(90, 90, 10, paint);		
				canvas.drawCircle(40, 90, 10, paint);
				canvas.drawCircle(90, 40, 10, paint);
				break;
		case 5: canvas.drawCircle(40, 40, 10, paint);
				canvas.drawCircle(90, 90, 10, paint);		
				canvas.drawCircle(40, 90, 10, paint);
				canvas.drawCircle(90, 40, 10, paint);	
				canvas.drawCircle(65, 65, 10, paint);
				break;
		case 6: canvas.drawCircle(40, 40, 10, paint);
				canvas.drawCircle(40, 65, 10, paint);
				canvas.drawCircle(90, 65, 10, paint);
				canvas.drawCircle(90, 90, 10, paint);		
				canvas.drawCircle(40, 90, 10, paint);
				canvas.drawCircle(90, 40, 10, paint);				
		break;
					

					
		}
		
	}
}

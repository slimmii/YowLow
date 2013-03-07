package com.pimpbunnies.yowlow.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Image;
import com.pimpbunnies.yowlow.threedee.Cube;

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
	private Context mContext;

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			shuffle();
		}
	};

	public Bitmap[] getFaces() {
		ArrayList<Bitmap> maps = new ArrayList<Bitmap>();
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.one));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.two));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.three));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.four));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.five));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.six));

		long seed = System.nanoTime();
		Collections.shuffle(maps, new Random(seed));
		
		Bitmap[] bm = new Bitmap[6];
		
		maps.toArray(bm);
		
		return bm;
	}
	
	public RealDieView(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Integer shuffle(final ShuffleCallback cb) {
		final Handler handler = new Handler();
		final Random rand = new Random();

		ArrayList<Bitmap> maps = new ArrayList<Bitmap>();
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.one));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.two));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.three));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.four));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.five));
		maps.add(BitmapFactory.decodeResource(getResources(), R.drawable.six));

		long seed = System.nanoTime();
		Collections.shuffle(maps, new Random(seed));
		
		
		fShuffleing = true;
		
		Bitmap[] bitmaps = new Bitmap[6];
		int i = 0;
		for (i=0;i<6;i++) {
			bitmaps[i] = maps.get(i);
		}

		mView.shuffle(cb);
		
		return 0;
	}
}

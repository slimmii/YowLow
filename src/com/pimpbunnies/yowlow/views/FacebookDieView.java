package com.pimpbunnies.yowlow.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.pimpbunnies.yowlow.MainActivity;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.R.drawable;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Image;
import com.pimpbunnies.yowlow.threedee.Cube;

public class FacebookDieView extends GenericDieView<Image> {

	private int fCounter = 0;
	private boolean fShuffleing = false;
	private Image fResult;
	protected List<Image> fSelectedGuests;

	protected MainActivity fContext;

	public FacebookDieView(MainActivity context) {
		super(context);
		this.fContext = context;
	}

	@Override
	public Bitmap[] getFaces() {
		Bitmap grumpy = BitmapFactory.decodeResource(getResources(),
				R.drawable.grumpy);

		final Random rand = new Random();

		if (fSelectedGuests == null) {
			BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(getContext());
			fSelectedGuests = db.getAllSelectedImages();
			db.close();
		}

		if (fSelectedGuests.size() > 0) {
			long seed = System.nanoTime();
			Collections.shuffle(fSelectedGuests, new Random(seed));
	
			fResult = getRandomGuest();
		}
		fShuffleing = true;

		Bitmap[] bitmaps = new Bitmap[6];
		int i = 0;
		for (i = 0; i < 6 && i < fSelectedGuests.size(); i++) {
			bitmaps[i] = BitmapFactory.decodeByteArray(fSelectedGuests.get(i)
					.getPicture(), 0,
					fSelectedGuests.get(i).getPicture().length);
		}
		for (; i < 6 ; i++) {
			bitmaps[i] = BitmapFactory.decodeResource(getResources(), R.drawable.grumpy);		
		}
		
		return bitmaps;
	}

	public Image getRandomGuest() {

		final Random rand = new Random();
		int max = fSelectedGuests.size();

		int randomNum = rand.nextInt(max);
		Image guest = fSelectedGuests.get(randomNum);

		return guest;
	}

	@Override
	public Image shuffle(final ShuffleCallback cb) {
		final Handler handler = new Handler();

		mView.setCube(new Cube(getFaces()));
		mView.shuffle(cb);


		return fResult;
	}
}

package com.pimpbunnies.yowlow.views;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.pimpbunnies.yowlow.MainActivity;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;
import com.pimpbunnies.yowlow.threedee.Cube;

public class FacebookDieView extends GenericDieView<Image> {

	private int fCounter = 0;
	private boolean fShuffleing = false;
	private Image fResult;
	protected List<Image> fSelectedGuests;
	private Group mGroup;
	private BirthdaySQLiteHelper mDb;
	protected MainActivity fContext;

	public FacebookDieView(MainActivity context, Group group) {
		super(context);
		this.mGroup = group;
		this.fContext = context;
		mDb = new BirthdaySQLiteHelper(getContext());
	}

	@Override
	public Bitmap[] getFaces() {
		Bitmap grumpy = BitmapFactory.decodeResource(getResources(),
				R.drawable.grumpy);

		final Random rand = new Random();

		fSelectedGuests = mDb.getImages(mGroup);

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

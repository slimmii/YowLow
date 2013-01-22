package com.pimpbunnies.yowlow.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;

import com.pimpbunnies.yowlow.MainActivity;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.R.drawable;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;

public class FacebookDieView extends GenericDieView<Guest> {

	private int fCounter = 0;
	private boolean fShuffleing = false;
	private Guest fResult;
	protected List<Guest> fSelectedGuests;

	protected MainActivity fContext;

	public OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			shuffle();
		}
	};
	
	public void setupClickListener() {
		this.setOnClickListener(onClickListener);
	}

	public FacebookDieView(MainActivity context) {
		super(context);

		resetImageToGrumpy();
		this.fContext = context;
		setupClickListener();
		this.setBackgroundResource(R.drawable.image_stroke_facebook_independant);
	}
	
	public void resetImageToGrumpy() {
		System.out.println("RESETTING TO GRUMPY");
		Bitmap grumpy = BitmapFactory.decodeResource(getResources(), R.drawable.grumpy);
		this.setImageBitmap(Bitmap.createScaledBitmap(grumpy, 120, 120, true));
	}

	@Override
	public Guest shuffle(final ShuffleCallback cb) {
		return shuffle(new ArrayList<Guest>());
	}
	
	public Guest getRandomGuest(ArrayList<Guest> ignore) {

		final Random rand = new Random();
		int max = fSelectedGuests.size();

		int randomNum = rand.nextInt(max);
		Guest guest = fSelectedGuests.get(randomNum);
		
		return guest;
	}

	@Override
	public Guest shuffle(ArrayList<Guest> ignore, final ShuffleCallback cb) {
		final Handler handler = new Handler();
		final Random rand = new Random();

		BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(getContext());
		fSelectedGuests = db.getAllSelectedGuests();
		if (fSelectedGuests.size() == 0) {
			resetImageToGrumpy();
			return null;
		}
		
		fResult = getRandomGuest(ignore);
		fShuffleing = true;
		handler.postDelayed(new Runnable() {
			public void run() {
				int max = fSelectedGuests.size();
				int randomNum = rand.nextInt(max);
				Guest guest = fSelectedGuests.get(randomNum);   
				Bitmap bitmap = BitmapFactory.decodeByteArray(guest.getPicture(), 0, guest.getPicture().length);
				int biggest = bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
				Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0, biggest, biggest);
				FacebookDieView.this.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, 120, 120, true));

				//fGuestName.setText(guest.getName());
				if (fCounter++ >= 30) {
					fCounter = 0;
					fShuffleing = false;

					bitmap = BitmapFactory.decodeByteArray(fResult.getPicture(), 0, fResult.getPicture().length);
					biggest = bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
					croppedBmp = Bitmap.createBitmap(bitmap, 0, 0, biggest, biggest);
					FacebookDieView.this.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, 120, 120, true));
					cb.shuffled();
				} else {
					handler.postDelayed(this, 5 + (fCounter * 5));
				}
			}
		}, 0);

		db.close();

		return fResult;
	}
}

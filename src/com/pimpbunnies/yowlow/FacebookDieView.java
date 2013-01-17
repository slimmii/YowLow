package com.pimpbunnies.yowlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;

public class FacebookDieView extends GenericDieView {
	
	private int fCounter = 0;
	private boolean fShuffleing = false;
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			shuffle();
		}
	};
	
	
	public FacebookDieView(Context context) {
		super(context);
		
		Bitmap grumpy = BitmapFactory.decodeResource(getResources(), R.drawable.grumpy);
		this.setImageBitmap(Bitmap.createScaledBitmap(grumpy, 120, 120, true));
		this.setOnClickListener(onClickListener);
	}
	
	@Override
	public void shuffle() {
		BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(getContext());
		final List<Guest> guests = db.getAllGuests();
		final List<Guest> selectedGuests = new ArrayList<Guest>();
		for (Guest guest : guests) {
			if (guest.isSelected()) {
				selectedGuests.add(guest);
			}
		}
		final Random rand = new Random();
		final int max = selectedGuests.size();


		final Handler handler = new Handler();
		fShuffleing = true;
		handler.postDelayed(new Runnable() {
			public void run() {
				int randomNum = rand.nextInt(max);
				Guest guest = selectedGuests.get(randomNum);   
				Bitmap bitmap = BitmapFactory.decodeByteArray(guest.getPicture(), 0, guest.getPicture().length);
				int biggest = bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
				Bitmap croppedBmp = Bitmap.createBitmap(bitmap, 0, 0, biggest, biggest);
				FacebookDieView.this.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, 120, 120, true));
				
				//fGuestName.setText(guest.getName());
				if (fCounter++ >= 30) {
					fCounter = 0;
					fShuffleing = false;
				} else {
					handler.postDelayed(this, 5 + (fCounter * 5));
				}
			}
		}, 0);
		db.close();
	}

}

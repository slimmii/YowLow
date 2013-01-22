package com.pimpbunnies.yowlow.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pimpbunnies.yowlow.MainActivity;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.R.drawable;
import com.pimpbunnies.yowlow.model.Guest;

import android.view.View;

public class DependantFacebookDieView extends FacebookDieView {

	public OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			fContext.onRollAllDice(null, DependantFacebookDieView.class);
		}
	};
	
	@Override
	public Guest getRandomGuest(ArrayList<Guest> ignore) {
		System.out.println("0------------getRandomGuest" + ignore.size());
		List<Guest> filteredGuests = new ArrayList<Guest>(fSelectedGuests);

		filteredGuests.removeAll(ignore);

		final Random rand = new Random();
		int max = filteredGuests.size();

		int randomNum = rand.nextInt(max);
		Guest guest = filteredGuests.get(randomNum);
		
		return guest;
	}
	
	@Override
	public void setupClickListener() {
		this.setOnClickListener(onClickListener);
	}
	
	public DependantFacebookDieView(MainActivity context) {
		super(context);
		this.setBackgroundResource(R.drawable.image_stroke_facebook_dependant);
		this.setupClickListener();
	}

}

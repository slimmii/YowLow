package com.pimpbunnies.yowlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.android.Facebook;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;


public class MainActivity extends FacebookActivity {
	private int fCounter = 0;
	private TextView fRandomAction;
	private ImageView fGuestImage;
	private TextView fGuestName;
	private boolean fShuffleing = false;

	public void onRandomButtonClicked(View view) {
		BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(MainActivity.this);
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

				fGuestImage.setImageBitmap(bitmap);
				fGuestName.setText(guest.getName());
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

	public void onGuestClicked(View view) {
		if (fShuffleing) {
			Toast.makeText(this, "Everyday I'm shuffleing!", Toast.LENGTH_SHORT).show();
		} else {
			new PictureClickedDialog().show(getSupportFragmentManager(),"");

		}
	}

	public void onShareClicked(View view) {
		postToWall("YOLO!! " + fRandomAction + " with + " + fGuestName);
	}
	

	public void postToWall(String message){
		Facebook facebook = new Facebook("146547195499640");
		Bundle parameters = new Bundle();
                parameters.putString("message", message);
                parameters.putString("description", "YOLO!");
                try {
        	        facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onImportButtonClicked(View view) {
		Intent intent = new Intent(this, ImportActivity.class);

		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fGuestImage = (ImageView) findViewById(R.id.activity_main_guest_image);
		fGuestName = (TextView) findViewById(R.id.activity_main_guest_name);
		fRandomAction = (TextView) findViewById(R.id.activity_main_random_action);
	}
}

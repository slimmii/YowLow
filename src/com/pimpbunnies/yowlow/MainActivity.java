package com.pimpbunnies.yowlow;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;

import com.facebook.FacebookActivity;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;
import com.pimpbunnies.yowlow.ui.DeviceShaked;
import com.pimpbunnies.yowlow.ui.Shaker;
import com.pimpbunnies.yowlow.views.DependantFacebookDieView;
import com.pimpbunnies.yowlow.views.FacebookDieView;
import com.pimpbunnies.yowlow.views.RealDieView;
import com.pimpbunnies.yowlow.views.ShuffleCallback;

public class MainActivity extends FacebookActivity {

	private GridLayout fDiceView;

	private ArrayList<Guest> rolledGuests;
	private ArrayList<Integer> rolledNumbers;
	private boolean fRollingAllDice = false;

	public void onRollAllDice(View view) {
		onRollAllDice(view, null);
	}
	
	ShuffleCallback shuffle = new ShuffleCallback() {
		
		@Override
		public void shuffled() {
			System.out.println("SHUFFLING DoNE");
			fRollingAllDice = false;
		}
	};
	
	public void onRollAllDice(View view, Class type) {
		if (!fRollingAllDice) {
			fRollingAllDice = true;
			rolledGuests = new ArrayList<Guest>();
			rolledNumbers = new ArrayList<Integer>();	
			BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(this);
			List<Guest> guests = db.getAllSelectedGuests();
			for (int i = 0; i < fDiceView.getChildCount(); i++) {
				View v = fDiceView.getChildAt(i);
				if (type == null || v.getClass() == type) {
					if (v instanceof DependantFacebookDieView) {
						if (guests.size() == 0) {
							((FacebookDieView) v).resetImageToGrumpy();
							fRollingAllDice = false;
						} else {
							if (rolledGuests.size() < guests.size()) {
								rolledGuests.add(((DependantFacebookDieView) v).shuffle(rolledGuests,shuffle));
							} else {
								((FacebookDieView) v).resetImageToGrumpy();
							}
						}
					} else if (v instanceof FacebookDieView) {
						if (guests.size() == 0) {
							fRollingAllDice = false;							
						} else {
							((FacebookDieView) v).shuffle(shuffle);
						}
					} else if (v instanceof RealDieView) {
						rolledNumbers.add(((RealDieView) v).shuffle(shuffle));
					}
				}
			}	
		}
	}
	
	private void askToGoToImportActivity() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		    		Intent intent = new Intent(MainActivity.this, ImportActivity.class);
		    		startActivity(intent);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("No friends selected/found. Do you want to import facebook data?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
	}

	public void onAddDiceClicked(View view) {
		final String items[] = {"Facebook (Dependant)", "Facebook (Independant)","Regular"};
		AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
		ab.setTitle("Dice kind");
		ab.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int choice) {
				BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(MainActivity.this);
				int size = db.getAllSelectedGuests().size();
				if(choice == 0) {
					if (size == 0) {
						askToGoToImportActivity();
					} else {
						fDiceView.addView(new DependantFacebookDieView(MainActivity.this));
					}
				}
				if(choice == 1) {
					if (size == 0) {
						askToGoToImportActivity();
					} else {
						fDiceView.addView(new FacebookDieView(MainActivity.this));
					}
				}				
				else if(choice == 2) {
					fDiceView.addView(new RealDieView(MainActivity.this));
				}
			}
		});
		ab.show();		
	}


	public void onImportButtonClicked(View view) {
		Intent intent = new Intent(this, ImportActivity.class);

		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		fDiceView = (GridLayout) findViewById(R.id.activity_main_dice_view);

		Shaker shaker = new Shaker(this, new DeviceShaked() {	
			@Override
			public void shaked() {
				onRollAllDice(null);
			}
		}, 2.5f);
	}





}

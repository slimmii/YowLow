package com.pimpbunnies.yowlow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;

import com.facebook.FacebookActivity;


public class MainActivity extends FacebookActivity {

	private GridLayout fDiceView;

	public void onRollAllDice(View view) {
	    for (int i = 0; i < fDiceView.getChildCount(); i++) {
	        View v = fDiceView.getChildAt(i);
	        if (v instanceof GenericDieView) {
	            ((GenericDieView) v).shuffle();
	        }
	    }		
	}
	
	public void onAddDiceClicked(View view) {
		final String items[] = {"Facebook","Regular"};
		AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
		ab.setTitle("Dice kind");
		ab.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int choice) {
				if(choice == 0) {
					fDiceView.addView(new FacebookDieView(MainActivity.this));
				}
				else if(choice == 1) {
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
		//
		//		fGuestImage = (ImageView) findViewById(R.id.activity_main_guest_image);
		//		fGuestName = (TextView) findViewById(R.id.activity_main_guest_name);
	}
}

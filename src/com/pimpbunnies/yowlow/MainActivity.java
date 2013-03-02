package com.pimpbunnies.yowlow;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;

import com.facebook.FacebookActivity;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;
import com.pimpbunnies.yowlow.ui.DeviceShaked;
import com.pimpbunnies.yowlow.ui.Shaker;
import com.pimpbunnies.yowlow.views.FacebookDieView;
import com.pimpbunnies.yowlow.views.GenericDieView;
import com.pimpbunnies.yowlow.views.RealDieView;
import com.pimpbunnies.yowlow.views.ShuffleCallback;

public class MainActivity extends FacebookActivity {

	private GridLayout fDiceView;

	private boolean fRollingAllDice = false;

	ShuffleCallback shuffle = new ShuffleCallback() {

		@Override
		public void shuffled() {
			System.out.println("SHUFFLING DoNE");
			fRollingAllDice = false;
		}
	};

	public void onRollAllDice() {
		if (fDiceView.getChildCount() == 0) {
			return;
		}
		if (!fRollingAllDice) {
			fRollingAllDice = true;
			for (int i = 0; i < fDiceView.getChildCount(); i++) {
				View v = fDiceView.getChildAt(i);
				if (v instanceof FacebookDieView) {
					BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(this);
					List<Guest> guests = db.getAllSelectedGuests();
					((FacebookDieView) v).shuffle(shuffle);
				} else if (v instanceof RealDieView) {
					((RealDieView) v).shuffle(shuffle);
				}
			}

		}
	}

	private void askToGoToImportActivity() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Intent intent = new Intent(MainActivity.this,
							ImportActivity.class);
					startActivity(intent);
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"No friends selected/found. Do you want to import facebook data?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}

	public void addNewDie(GenericDieView die) {
		fDiceView.addView(die);
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fDiceView = (GridLayout) findViewById(R.id.activity_main_dice_view);

		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		// actionBar.setHomeAction(new IntentAction(this, createIntent(this),
		// R.drawable.ic_title_home_demo));
		actionBar.setTitle("Board");

		actionBar.addAction(new ImportAction());
		actionBar.addAction(new AddDiceAction());
		actionBar.addAction(new ThrowDiceAction());

		Shaker shaker = new Shaker(this, new DeviceShaked() {
			@Override
			public void shaked() {
				onRollAllDice();
			}
		}, 2.5f);
	}

	private class AddDiceAction extends AbstractAction {

		public AddDiceAction() {
			super(R.drawable.ic_diceadd);
		}

		@Override
		public void performAction(View view) {
			DiceKindDialog dialog = new DiceKindDialog(MainActivity.this);
			dialog.show();
		}

	}

	private class ThrowDiceAction extends AbstractAction {

		public ThrowDiceAction() {
			super(R.drawable.ic_dicethrow);
		}

		@Override
		public void performAction(View view) {
			onRollAllDice();
		}

	}
	
	private class ImportAction extends AbstractAction {

		public ImportAction() {
			super(R.drawable.ic_import);
		}

		@Override
		public void performAction(View view) {
			Intent intent = new Intent(MainActivity.this, ImportActivity.class);

			startActivity(intent);
		}

	}	
	
	

}

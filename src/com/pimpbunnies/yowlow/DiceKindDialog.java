package com.pimpbunnies.yowlow;

import java.util.ArrayList;

import com.pimpbunnies.yowlow.views.DependantFacebookDieView;
import com.pimpbunnies.yowlow.views.FacebookDieView;
import com.pimpbunnies.yowlow.views.RealDieView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class DiceKindDialog extends Dialog {

	private OnClickListener mListener;
	private Spinner mListview;
	private DiceKindAdapter mDiceKinds;
	private MainActivity mMainActivity;
	private Button mOkButton;
	private Button mCancelButton;
	
	public DiceKindDialog(Context context) {
		super(context);
		this.setTitle("Choose your type of dice");
		mMainActivity = (MainActivity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_dicekind);	
		
		ArrayList<DiceKind> kinds = new ArrayList<DiceKind>();
		
		final DiceKind regular = new DiceKind("Regular Dice", 
				"Just a plain old die", 
				getContext().getResources().getDrawable(R.drawable.ic_launcher));
		final DiceKind facebookDep = new DiceKind("Facebook Dice (independant)",
				"An independant dice. One friend can appear on more than one dice after rolling.", 
				getContext().getResources().getDrawable(R.drawable.ic_facebook));
		final DiceKind facebookIndep = new DiceKind("Facebook Dice (dependant)", 
				"A dependant dice. If a friend has been randomly chosen it will not appear on the other dice.", 
				getContext().getResources().getDrawable(R.drawable.ic_facebook));
		
		kinds.add(regular);
		kinds.add(facebookDep);
		kinds.add(facebookIndep);
		
		mDiceKinds = new DiceKindAdapter(this.getContext(),  kinds);
		
		mListview = (Spinner) findViewById(R.id.dialog_dicekind_scrollview_listview);
		mListview.setAdapter(mDiceKinds);
		
		mOkButton = (Button) findViewById(R.id.dialog_dicekind_okbutton);
		mOkButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				DiceKindDialog.this.cancel();				
				if (mListview.getSelectedItem() == regular) {					
					mMainActivity.addNewDie(new RealDieView(mMainActivity));
				}
				if (mListview.getSelectedItem() == facebookDep) {					
					mMainActivity.addNewDie(new DependantFacebookDieView(mMainActivity));
				}
				if (mListview.getSelectedItem() == facebookIndep) {					
					mMainActivity.addNewDie(new FacebookDieView(mMainActivity));
				}
			}
		});
		
		mCancelButton = (Button) findViewById(R.id.dialog_dicekind_cancelbutton);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DiceKindDialog.this.cancel();
			}
		});
	}
	
	

}

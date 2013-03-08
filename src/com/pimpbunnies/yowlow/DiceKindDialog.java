package com.pimpbunnies.yowlow;

import java.util.ArrayList;
import java.util.List;

import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
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
	private BirthdaySQLiteHelper mDb;
	
	public DiceKindDialog(Context context, BirthdaySQLiteHelper db) {
		super(context);
		this.setTitle("Choose your type of dice");
		this.mDb = db;
		mMainActivity = (MainActivity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_dicekind);	
		
		final ArrayList<DiceKind> kinds = new ArrayList<DiceKind>();
		final List<Group> groups = mDb.getGroups();
		
		
		final DiceKind regular = new DiceKind("Regular Dice", 
				"Just a plain old die", 
				getContext().getResources().getDrawable(R.drawable.ic_launcher));
		kinds.add(regular);
		for (Group group : groups) {
			kinds.add(new DiceKind(group.getName(),
					"Customized dice", 
					getContext().getResources().getDrawable(R.drawable.ic_facebook)));
		}
		
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
				
				for (int i=1; i<kinds.size();i++) {
					DiceKind kind = kinds.get(i);
					if (mListview.getSelectedItem() == kind) {
						FacebookDieView facebook = new FacebookDieView(mMainActivity, groups.get(i-1));
						mMainActivity.addNewDie(facebook);
					}	
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

package com.pimpbunnies.yowlow;

import java.util.ArrayList;

import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;
import com.pimpbunnies.yowlow.views.FacebookDieView;
import com.pimpbunnies.yowlow.views.RealDieView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class ImageManageDialog extends Dialog {

	private OnClickListener mListener;
	private Spinner mListview;
	private ArrayAdapter<Group> mGroups;
	private ImportActivity mImportActivity;
	private Button mOkButton;
	private Button mCloseButton;
	private Image mImage;
	private BirthdaySQLiteHelper mDb;
	private Button dialog_imagemanage_add_button;
	private EditText dialog_imagemanage_groupname_text;
	private Button dialog_imagemanage_groupname_add_button;
	
	public ImageManageDialog(Context context, Image image, BirthdaySQLiteHelper db) {
		super(context);
		this.setTitle(image.getName());
		this.mImage = image;
		this.mDb = db;
		this.mImportActivity = (ImportActivity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_imagemanage);	
	
		dialog_imagemanage_add_button = (Button) findViewById(R.id.dialog_imagemanage_add_button);
		dialog_imagemanage_add_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Group group = (Group) mListview.getSelectedItem();
				mDb.addImageToGroup(mImage, group);
			}
		});
		
		dialog_imagemanage_groupname_text = (EditText) findViewById(R.id.dialog_imagemanage_groupname_text);
		dialog_imagemanage_groupname_add_button = (Button) findViewById(R.id.dialog_imagemanage_groupname_add_button);
		dialog_imagemanage_groupname_add_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Group group = new Group(dialog_imagemanage_groupname_text.getText().toString());
				mDb.createGroup(group);
				mGroups.add(group);
				mGroups.notifyDataSetChanged();
			}
		});
		
		mGroups = new ArrayAdapter<Group>(this.getContext(), android.R.layout.simple_list_item_1);
		
		mListview = (Spinner) findViewById(R.id.dialog_imagemanage_scrollview_listview);
		mListview.setAdapter(mGroups);
		
		for (Group group : mDb.getGroups()) {
			mGroups.add(group);
		}
		
		mCloseButton = (Button) findViewById(R.id.dialog_imagemanage_closebutton);
		mCloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageManageDialog.this.cancel();
			}
		});
	}
	
	

}

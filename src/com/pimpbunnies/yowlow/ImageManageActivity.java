package com.pimpbunnies.yowlow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;

public class ImageManageActivity extends Activity {

	private OnClickListener mListener;
	private ListView mListview;
	private ImageManageArrayAdapter mGroups;
	private ImportActivity mImportActivity;
	private Button mOkButton;
	private Button mCloseButton;
	private ImageView activity_imagemanage_image;
	private TextView activity_imagemanage_name;
	private Image mImage;
	private BirthdaySQLiteHelper mDb = new BirthdaySQLiteHelper(ImageManageActivity.this);
		
	private ImageButton dialog_imagemanage_add_button;
	private EditText dialog_imagemanage_groupname_text;
	private Button activity_imagemanage_addNewDie;
	
	public ImageManageActivity() {	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imagemanage);	

		Bundle b = getIntent().getExtras();
		mImage = b.getParcelable("image");	
		
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		
		actionBar.setTitle("Managing " + mImage.getName());

        actionBar.setHomeAction(new IntentAction(this, ImportActivity.createIntent(this), R.drawable.ic_back));
        actionBar.setDisplayHomeAsUpEnabled(false);
	
		activity_imagemanage_image = (ImageView) findViewById(R.id.activity_imagemanage_image);
		Bitmap bitmap = BitmapFactory.decodeByteArray(mImage.getPicture(), 0,
				mImage.getPicture().length);
		activity_imagemanage_image.setImageBitmap(bitmap);
		activity_imagemanage_name = (TextView) findViewById(R.id.activity_imagemanage_name);
		activity_imagemanage_name.setText(mImage.getName());

//		activity_imagemanage_addNewDie = (Button) findViewById(R.id.activity_imagemanage_addNewDie);
//		activity_imagemanage_addNewDie.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				/*
//				Group group = new Group(dialog_imagemanage_groupname_text.getText().toString());
//				mDb.createGroup(group);
//				mGroups.add(group);
//				mGroups.notifyDataSetChanged();
//				*/
//			}
//		});
		
		mGroups = new ImageManageArrayAdapter(this, mDb.getGroups(), mDb);		
		mListview = (ListView) findViewById(R.id.activity_imagemanage_list);
		mListview.setAdapter(mGroups);
	}
	
	public void addImageToGroup(Group g) {
		System.out.println("CLIIIIIICK");
		mDb.addImageToGroup(mImage, g);
		mGroups.notifyDataSetChanged();
	}
	
	public void removeImageFromGroup(Group g) {
		System.out.println("Removing " + mImage.getName() + " from " + g.getName());
		
		mDb.removeImageFromGroup(mImage, g);
		mGroups.notifyDataSetChanged();
	}
	
	

}

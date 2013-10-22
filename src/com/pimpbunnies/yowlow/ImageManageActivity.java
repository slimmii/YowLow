package com.pimpbunnies.yowlow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	public void addGroupName(DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Title");

		// Set up the input
		dialog_imagemanage_groupname_text = new EditText(this);
		dialog_imagemanage_groupname_text.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(dialog_imagemanage_groupname_text);

		// Set up the buttons
		builder.setPositiveButton("OK", listener);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
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

		activity_imagemanage_addNewDie = (Button) findViewById(R.id.activity_imagemanage_addNewDie);
		activity_imagemanage_addNewDie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addGroupName(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Group group = new Group(dialog_imagemanage_groupname_text.getText().toString());
						mDb.createGroup(group);
						mGroups.add(group);
						mGroups.notifyDataSetChanged();						
					}
				});
			}
		});
		
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

	public void removeGroup(Group g) {
		mDb.removeGroup(g);
		mGroups.remove(g);
		mGroups.notifyDataSetChanged();
	}
	
	

}

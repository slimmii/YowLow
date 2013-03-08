package com.pimpbunnies.yowlow.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.pimpbunnies.yowlow.MainActivity;
import com.pimpbunnies.yowlow.R;

public class CameraActivity extends Activity {

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    private ImageView img;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		// actionBar.setHomeAction(new IntentAction(this, createIntent(this),
		// R.drawable.ic_title_home_demo));
		actionBar.setTitle("Image Dice");
		
		actionBar.addAction(new CameraImportAction());

        
        img = (ImageView)findViewById(R.id.ImageView1);
 
    }
    
	private class CameraImportAction extends AbstractAction {

		public CameraImportAction() {
			super(R.drawable.ic_camera);
		}

		@Override
		public void performAction(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);

		}

	}	

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                img.setImageURI(selectedImageUri);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
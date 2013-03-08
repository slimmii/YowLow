package com.pimpbunnies.yowlow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Image;

public class ImportActivity extends FacebookActivity {

	/** List of Views **/
	private EditText activity_import_search_edittext;

	private ListView activity_import_list;
	private ProgressDialog fImportDialog;
	private ProgressBar fDownloadProgressBar;
	private ProgressDialog fSaveDialog;
	/** End of list **/
	
    private static final int SELECT_PICTURE = 1;

	private DownloadImageOperation mDownloadImageOperation;
	
	private boolean mImporting = false;
	
	private List<Image> guests = new ArrayList<Image>();

	private List<String> permissions = Arrays.asList("read_friendlists","user_about_me","friends_about_me","user_activities","friends_activities",
			"user_birthday","friends_birthday","user_checkins","friends_checkins",
			"user_education_history","friends_education_history",
			"user_events","friends_events",
			"user_groups","friends_groups",
			"user_hometown","friends_hometown",
			"user_interests","friends_interests",
			"user_likes","friends_likes",
			"user_location","friends_location",
			"user_notes","friends_notes",
			"user_photos","friends_photos",
			"user_questions","friends_questions",
			"user_relationships","friends_relationships",
			"user_relationship_details","friends_relationship_details",
			"user_religion_politics","friends_religion_politics",
			"user_status","friends_status",
			"user_subscriptions","friends_subscriptions",
			"user_videos","friends_videos",
			"user_website","friends_website",
			"user_work_history","friends_work_history","email");

	private GuestAdapter fGuestAdapter;
	private boolean fFacebookLinkActive = false;
	private BirthdaySQLiteHelper mDb = new BirthdaySQLiteHelper(ImportActivity.this);
	
	public void onSaveButtonClicked() {
		System.out.println("ImportActivity.onSaveButtonClicked()");
		
		if (!mImporting) {		
			Image[] array = new Image[guests.size()];
			array = guests.toArray(array);
			new SaveOperation().execute(array);
 		} else {
 			Toast.makeText(this, "You cannot save during import", Toast.LENGTH_SHORT).show();
 		}

	}

	public List<Image> getGuests() {
		List<Image> guests = mDb.getImages();
		return guests;
	}

	public void onClearButtonClicked() {
		if (mDownloadImageOperation != null) {
			// RESET IMPORT
			mDownloadImageOperation.cancel(true);
			fDownloadProgressBar.setMax(0);
			fDownloadProgressBar.setProgress(0);
		}		
		mDb.flush();
		guests.clear();
		fGuestAdapter.filter();
		
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}

	public void onImportButtonClicked() {
		
		if (!isNetworkAvailable()) {
			Toast.makeText(this, "Facebook import failed: there is no network available.", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (mDownloadImageOperation != null) {
			// RESET IMPORT
			mDownloadImageOperation.cancel(true);
		}
		
		System.out.println("ImportActivity.onImportButtonClicked()");
		if (fFacebookLinkActive) {
			Session session = Session.getActiveSession();
			System.out.println(session.getPermissions());
			System.out.println(session.getAccessToken());
			
			fImportDialog = new ProgressDialog(ImportActivity.this);
			fImportDialog.setMessage("Executing Request...");
			fImportDialog.setCancelable(false);
			fImportDialog.show();
			
			guests.clear();

			// make request to the /me API
			Request.executeGraphPathRequestAsync(session, "/me/friends",
					new Callback() {
				@Override
				public void onCompleted(Response response) {
					fImportDialog.dismiss();
					try {
						mImporting = true;
						JSONObject obj = response.getGraphObject().getInnerJSONObject();
						List<String> list = new ArrayList<String>();
						JSONArray array;
						array = obj.getJSONArray("data");

						// Stage 1 - Prepare
						// 1. Convert JSON Array to ArrayList of Guests and
						try {
							for(int i = 0 ; i < array.length() ; i++){
								String id = array.getJSONObject(i).getString("id");
								String name = array.getJSONObject(i).getString("name");
								Image guest = new Image(name, "facebook://" + id);
								guests.add(guest);
								System.out.println(guest.getName() + " added!");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						// Stage 2 - Sorting both lists the same way.
						Collections.sort(guests, new Comparator<Image>() {
							@Override
							public int compare(Image lhs, Image rhs) {
								return lhs.getName().compareTo(rhs.getName());
							}
						});
			
						fGuestAdapter.notifyDataSetChanged();
						fGuestAdapter.filter();
						
						// Stage 3 - We now want to asynchronously download the profile images of the guests
						mDownloadImageOperation = new DownloadImageOperation(false, guests.size());
						mDownloadImageOperation.execute(guests.toArray(new Image[guests.size()]));
						//new CreateGuestsOperation().execute(array);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			//Request.executeBatchAsync(request);      

		} else {
			System.out.println("NO SESSION YET");
		}
	}
	
	private class SaveOperation extends AsyncTask<Image, Void, Void> {
		private BirthdaySQLiteHelper fDb;
		
		public SaveOperation() {	
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			fSaveDialog = new ProgressDialog(ImportActivity.this);
			fSaveDialog.setMessage("Saving...");
			fSaveDialog.setCancelable(false);
			fSaveDialog.show();      
			fDb.flush();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			fDb.close();
			fSaveDialog.dismiss();
		}
		
		@Override
		protected Void doInBackground(Image... params) {
			for (int i=0;i<params.length;i++) {
				fDb.createImage(params[i]);
			}
			return null;
		}
	}

	private class DownloadImageOperation extends AsyncTask<Image, Bitmap, List<Bitmap>> {
		private int mRequests;
		private boolean mLowQuality;

		public DownloadImageOperation(boolean lowQuality, int i) {
			this.mRequests = i;
			this.mLowQuality = lowQuality;
		}

		@Override
		protected List<Bitmap> doInBackground(Image... requests) {
			List<Bitmap> bitmaps = new ArrayList<Bitmap>(); 
			for (Image request : requests) {
				
				if (request.getPictureSource().startsWith("facebook://")) {
					String facebookId = request.getPictureSource().replace("facebook://", "");
					String pictureUrl = "http://graph.facebook.com/" + facebookId + "/picture?type=" + (mLowQuality?"small":"large");
					Bitmap bm = getBitmapFromURL(pictureUrl);
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, 64,64, false);
					bm.recycle(); // Recycle the memory!
					request.setPictureBitmap(scaledBitmap);
					mDb.createImage(request);
					publishProgress(scaledBitmap);
				}
			}
			return bitmaps;
		}    

		public Bitmap getBitmapFromURL(String src) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(src).getContent());
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			super.onProgressUpdate(values);
			fGuestAdapter.notifyDataSetChanged();
			fDownloadProgressBar.incrementProgressBy(1);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println(">>>>>>>>>>>>>>PREEXECUTE!");
			fDownloadProgressBar.setMax(mRequests);
			fDownloadProgressBar.setProgress(0);
 
		}

		@Override
		protected void onPostExecute(List<Bitmap> bitmaps) {
			super.onPostExecute(bitmaps);
			mDb.close();
			mImporting = false;
		}
	}
	
	TextWatcher searchTextWatcher = new TextWatcher() {
	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {	    	
	    	System.out.println("Filtering " + arg0);
	    	fGuestAdapter.setFilterString(arg0.toString());
	    	fGuestAdapter.filter();
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	            int arg3) {
	    }

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private class ImportAction extends AbstractAction {

		public ImportAction() {
			super(R.drawable.ic_facebook_icon);
		}

		@Override
		public void performAction(View view) {
			onImportButtonClicked();
		}

	}	
	
	private class SaveAction extends AbstractAction {

		public SaveAction() {
			super(R.drawable.ic_save);
		}

		@Override
		public void performAction(View view) {
			onSaveButtonClicked();
		}

	}	
	
	private class ClearAction extends AbstractAction {

		public ClearAction() {
			super(R.drawable.ic_clear);
		}

		@Override
		public void performAction(View view) {
			onClearButtonClicked();
		}

	}
	
	private class CameraAction extends AbstractAction {

		public CameraAction() {
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
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final Uri selectedImageUri = data.getData();
                try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
					final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 64,64, false);
					bitmap.recycle();
					// Set an EditText view to get user input 
					final EditText input = new EditText(this);
					new AlertDialog.Builder(ImportActivity.this)
				    .setTitle("Enter ")
				    .setMessage("ZOMGS")
				    .setView(input)
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	String name = input.getText().toString();
				            if (!name.toString().equals("")) {
				            	Image guest = new Image(name, "image://" + selectedImageUri.getPath());
				            	guest.setPictureBitmap(scaledBitmap);
				            	guests.add(guest);
				            	mDb.createImage(guest);
				            	fGuestAdapter.notifyDataSetChanged();
				            	fGuestAdapter.filter();
				            } else {
				            	
				            }
				        }
				    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            // Do nothing.
				        }
				    }).show();
					
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
		
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		
		actionBar.setTitle("Facebook Import");
		
		actionBar.addAction(new CameraAction());
		actionBar.addAction(new ImportAction());
		actionBar.addAction(new ClearAction());		
		
        actionBar.setHomeAction(new IntentAction(this, MainActivity.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setDisplayHomeAsUpEnabled(false);
		
		activity_import_search_edittext = (EditText) findViewById(R.id.activity_import_search_edittext);
		
		final ToggleButton toggleFacebook = (ToggleButton) findViewById(R.id.activity_import_facebook_filter_toggle);
		final ToggleButton toggleImage = (ToggleButton)findViewById(R.id.activity_import_image_filter_toggle);

		toggleFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (toggleFacebook.isChecked() && toggleImage.isChecked()) {
					fGuestAdapter.setFilterSource("");
				} else if (toggleFacebook.isChecked()) {
					fGuestAdapter.setFilterSource("facebook://");
				} else if (toggleImage.isChecked()) {
					fGuestAdapter.setFilterSource("image://");
				} else {
					fGuestAdapter.setFilterSource("null://");
				}
				fGuestAdapter.filter();
			}
		});
		
		toggleImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (toggleFacebook.isChecked() && toggleImage.isChecked()) {
					fGuestAdapter.setFilterSource("");
				} else if (toggleImage.isChecked()) {
					fGuestAdapter.setFilterSource("image://");
				} else if (toggleFacebook.isChecked()) {
					fGuestAdapter.setFilterSource("facebook://");
				} else {
					fGuestAdapter.setFilterSource("null://");
				}
				fGuestAdapter.filter();
			}
		});
		
		activity_import_search_edittext.addTextChangedListener(searchTextWatcher);
		activity_import_list = (ListView) findViewById(R.id.activity_import_list);		
		
		fDownloadProgressBar = (ProgressBar) findViewById(R.id.activity_import_progress);
		
		Session session = initFacebookSession(this);
		Session.setActiveSession(session);

		StatusCallback statusCallback = new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				System.out
				.println("ImportActivity.onCreate(...).new StatusCallback() {...}.call()" + state) ;
				if (state.equals(SessionState.CLOSED_LOGIN_FAILED)) {
					Toast.makeText(ImportActivity.this, "Cannot login to facebook", 1000).show();
				}
			}
		};

		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			System.out.println("Case1");
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback).setPermissions(permissions));
		} else if (!session.isOpened() && !session.isClosed()) {
			System.out.println("Case2");
			OpenRequest req = new Session.OpenRequest(this);
			req.setCallback(statusCallback);
			req.setPermissions(permissions);
			session.openForRead(req);
		} else {
			System.out.println("Case3");
			Session.openActiveSession(this, true, statusCallback);
		}
		
		guests = getGuests();
		
		fGuestAdapter = new GuestAdapter(this, R.layout.guest_list_item, guests);
		activity_import_list.setAdapter(fGuestAdapter);
		
		activity_import_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Image image = (Image) arg0.getItemAtPosition(arg2);
				ImageManageDialog dialog = new ImageManageDialog(ImportActivity.this, image, mDb);
				dialog.show();
			}

		});
		
		
		fGuestAdapter.filter();
		
		//    
		//    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(this);
		//    db.addGuest(new Guest(0, "Andie Similon", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
		//    List<Guest> guests = db.getAllContacts();
		//    System.out.println(guests);
	}

	private static Session initFacebookSession(Context context) {
		Session session = Session.getActiveSession();
		if (session != null) {
			return session;
		}
		if (session == null) {
			session = new Session(context);
		}
		return session;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		System.out.println("MainActivity.onSessionStateChange()");
		// user has either logged in or not ...
		if (state.isOpened()) {
			fFacebookLinkActive = true;
		} else if (state.isClosed()) {
			fFacebookLinkActive = false;
		}
	}
}

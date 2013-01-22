package com.pimpbunnies.yowlow;

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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.VoicemailContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;

public class ImportActivity extends FacebookActivity {

	/** List of Views **/
	private Button activity_import_import_button;
	private Button activity_import_open_session_button;
	private Button activity_import_clear_button;
	private EditText activity_import_facebook_graph_path;


	private ListView activity_import_list;
	private ProgressDialog fImportDialog;
	private ProgressDialog fDownloadDialog;
	private ProgressDialog fSaveDialog;
	/** End of list **/


	private List<Guest> guests = new ArrayList<Guest>();
	private GuestAdapter guestAdapter;

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

	public void onSaveButtonClicked(View view) {
		System.out.println("ImportActivity.onSaveButtonClicked()");
		
		Guest[] array = new Guest[guests.size()];
		array = guests.toArray(array);
		new SaveOperation().execute(array);

	}

	public List<Guest> getGuests() {
		BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(ImportActivity.this);
		List<Guest> guests = db.getAllGuests();
		if (db != null) {
			db.close();
		}      
		return guests;
	}

	public void onClearButtonClicked(View view) {
		BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(ImportActivity.this);
		db.flush();
	}

	public void onImportButtonClicked(View view) {
		System.out.println("ImportActivity.onImportButtonClicked()");
		if (fFacebookLinkActive) {
			Session session = Session.getActiveSession();
			System.out.println(session.getPermissions());
			System.out.println(session.getAccessToken());

			guests.clear();
			
			fImportDialog = new ProgressDialog(ImportActivity.this);
			fImportDialog.setMessage("Executing Request...");
			fImportDialog.setCancelable(false);
			fImportDialog.show();

			// make request to the /me API
			Request.executeGraphPathRequestAsync(session, activity_import_facebook_graph_path.getText().toString(),
					new Callback() {
				@Override
				public void onCompleted(Response response) {
					fImportDialog.dismiss();
					try {

						JSONObject obj = response.getGraphObject().getInnerJSONObject();
						List<String> list = new ArrayList<String>();
						JSONArray array;
						array = obj.getJSONArray("data");

						// Stage 1 - Prepare
						// 1. Convert JSON Array to ArrayList of Guests and
						// 2. Also prepare an array of download requests for images.
						DownloadRequest[] requests = new DownloadRequest[array.length()];
						try {
							for(int i = 0 ; i < array.length() ; i++){
								String id = array.getJSONObject(i).getString("id");
								String name = array.getJSONObject(i).getString("name");
								Guest guest = new Guest(0, name, false);
								guests.add(guest);
								System.out.println(guest.getName() + " added!");
								requests[i] = new DownloadRequest(id, guest);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// Stage 2 - We now want to asynchronously download the profile images of the guests
						new DownloadImageOperation(guests.size()).execute(requests);


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
	
	private class SaveOperation extends AsyncTask<Guest, Void, Void> {
		private BirthdaySQLiteHelper fDb;
		
		public SaveOperation() {	
			fDb = new BirthdaySQLiteHelper(ImportActivity.this);

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
		protected Void doInBackground(Guest... params) {
			for (int i=0;i<params.length;i++) {
				fDb.addGuest(params[i]);
			}
			return null;
		}
	}

	private class DownloadImageOperation extends AsyncTask<DownloadRequest, Bitmap, List<Bitmap>> {
		private int requests;

		public DownloadImageOperation(int i) {
			this.requests = i;
		}

		@Override
		protected List<Bitmap> doInBackground(DownloadRequest... requests) {
			List<Bitmap> bitmaps = new ArrayList<Bitmap>(); 
			for (DownloadRequest request : requests) {
				String pictureUrl = "http://graph.facebook.com/" + request.getId() + "/picture?type=large";
				Bitmap bm = getBitmapFromURL(pictureUrl);
				request.getGuest().setPicture(bm);
				publishProgress(bm);
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
			fDownloadDialog.incrementProgressBy(1);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println(">>>>>>>>>>>>>>PREEXECUTE!");
			fDownloadDialog = new ProgressDialog(ImportActivity.this);
			fDownloadDialog.setMessage("Downloading Images");
			fDownloadDialog.setCancelable(false);
			fDownloadDialog.setMax(requests);
			fDownloadDialog.setProgress(0);
			fDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			fDownloadDialog.show();        
		}

		@Override
		protected void onPostExecute(List<Bitmap> bitmaps) {
			super.onPostExecute(bitmaps); 
			fDownloadDialog.dismiss();
			Collections.sort(guests, new Comparator<Guest>() {
				@Override
				public int compare(Guest lhs, Guest rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			guestAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		activity_import_import_button = (Button) findViewById(R.id.activity_import_import_button);
		activity_import_list = (ListView) findViewById(R.id.activity_import_list);
		activity_import_facebook_graph_path = (EditText) findViewById(R.id.activity_import_facebook_graph_path);
		activity_import_facebook_graph_path.setText("/543424479010541/invited");
		
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
		
		guestAdapter = new GuestAdapter(this, R.layout.guest_list_item, guests);
		activity_import_list.setAdapter(guestAdapter);
		activity_import_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out
						.println("ImportActivity.onCreate(...).new OnItemClickListener() {...}.onItemClick()" + arg2);
				Guest selectedGuest = guestAdapter.getItem(arg2);
				selectedGuest.setSelected(!selectedGuest.isSelected());
				guestAdapter.notifyDataSetChanged();
			}
			
		});
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

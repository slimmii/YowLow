package com.pimpbunnies.yowlow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Request.Callback;
import com.facebook.widget.LoginButton;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ImportActivity extends FacebookActivity {

  /** List of Views **/
  private Button activity_import_import_button;
  private Button activity_import_open_session_button;
  private Button activity_import_clear_button;
  private ListView activity_import_guests_list;
  private ProgressDialog fImportDialog;
  private ProgressDialog fDownloadDialog;
  /** End of list **/


  private GuestAdapter fGuestAdapter;
  private boolean fFacebookLinkActive = false;

  public void onPrintButtonClicked(View view) {
    System.out.println("ImportActivity.onPrintButtonClicked()");
    List<Guest> guests = getGuests();
    for (Guest guest : guests) {
      System.out.println(guest.getName());
    }
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

      fImportDialog = new ProgressDialog(ImportActivity.this);
      fImportDialog.setMessage("Executing Request...");
      fImportDialog.setCancelable(false);
      fImportDialog.show();

      // make request to the /me API
      Request.executeGraphPathRequestAsync(session, "/281666475288890/attending",
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
            List<Guest> guests = new ArrayList<Guest>();
            DownloadRequest[] requests = new DownloadRequest[array.length()];
            try {
              for(int i = 0 ; i < array.length() ; i++){
                String id = array.getJSONObject(i).getString("id");
                String name = array.getJSONObject(i).getString("name");
                Guest guest = new Guest(0, name);
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
  //
  //  private class CreateGuestsOperation extends AsyncTask<JSONArray, Void, List<Guest>> {
  //    @Override
  //    protected List<Guest> doInBackground(JSONArray... params) {
  //      List<Guest> importedGuests = new ArrayList<Guest>();
  //
  //      JSONArray array = params[0];
  //      try {
  //        // Stage 1 - Import guests in ArrayList
  //        for(int i = 0 ; i < array.length() ; i++){
  //          String id = array.getJSONObject(i).getString("id");
  //          String name = array.getJSONObject(i).getString("name");
  //          Guest guest = new Guest(0, name);
  //          importedGuests.add(guest);
  //          new DownloadImageOperation().execute(id, guest);
  //          System.out.println(guest.getName() + " added!");        
  //        }
  //
  //        mDialog.dismiss();
  //      } catch (JSONException e) {
  //        e.printStackTrace();
  //      }
  //
  //      return importedGuests;
  //    }

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
    }
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_import);

    activity_import_import_button = (Button) findViewById(R.id.activity_import_import_button);
    activity_import_guests_list = (ListView) findViewById(R.id.activity_import_imported_guests_list);

    LoginButton authButton = (LoginButton) findViewById(R.id.activity_import_facebook_login);
    authButton.setReadPermissions(Arrays.asList("read_friendlists","user_about_me","friends_about_me","user_activities","friends_activities",
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
        "user_work_history","friends_work_history","email"));

    List<Guest> guests = getGuests();
    fGuestAdapter = new GuestAdapter(this, R.id.activity_import_imported_guests_list, guests);
    activity_import_guests_list.setAdapter(fGuestAdapter);

    //    
    //    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(this);
    //    db.addGuest(new Guest(0, "Andie Similon", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
    //    List<Guest> guests = db.getAllContacts();
    //    System.out.println(guests);
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

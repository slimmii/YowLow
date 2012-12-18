package com.pimpbunnies.birthday;

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
import com.pimpbunnies.birthday.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.birthday.model.Guest;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ImportActivity extends FacebookActivity {
  private Button activity_import_import_button;
  private Button activity_import_open_session_button;
  private boolean facebookLinkActive = false;

  public void onPrintButtonClicked(View view) {
    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(ImportActivity.this);
    List<Guest> guests = db.getAllGuests();
    
    for (Guest guest : guests) {
      System.out.println(guest.getName());
    }
    
    db.close();
  }
  
  public void onRandomGuestButtonClicked(View view) {
    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(ImportActivity.this);
    List<Guest> guests = db.getAllGuests();
    
    int max = guests.size();
    
    Random rand = new Random();
    int randomNum = rand.nextInt(max);
    
    System.out.println(guests.get(randomNum).getName());
    
    db.close();
  }
  
  public void onImportButtonClicked(View view) {
    System.out.println("MainActivity.onClick()");
    if (facebookLinkActive) {
      Session session = Session.getActiveSession();
      System.out.println(session.getPermissions());
      System.out.println(session.getAccessToken());
      // make request to the /me API
      Request.executeGraphPathRequestAsync(session, "/470786339640339/attending",
          new Callback() {
        @Override
        public void onCompleted(Response response) {
          JSONObject obj = response.getGraphObject().getInnerJSONObject();
          List<String> list = new ArrayList<String>();
          JSONArray array;
          try {
            array = obj.getJSONArray("data");
            BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(ImportActivity.this);
            db.flush(); // Delete current guests!
            for(int i = 0 ; i < array.length() ; i++){
              String id = array.getJSONObject(i).getString("id");
              String name = array.getJSONObject(i).getString("name");
              String pictureUrl = "http://graph.facebook.com/" + id + "/picture?type=large";
              Bitmap picture = getBitmapFromURL(pictureUrl);
              
              db.addGuest(new Guest(0, name, picture));
            }
            db.close();
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


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_import);

    activity_import_import_button = (Button) findViewById(R.id.activity_import_import_button);

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

    //    
    //    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(this);
    //    db.addGuest(new Guest(0, "Andie Similon", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
    //    List<Guest> guests = db.getAllContacts();
    //    System.out.println(guests);
  }

  public static Bitmap getBitmapFromURL(String src) {
    try {
      URL url = new URL(src);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);
      return myBitmap;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
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
      facebookLinkActive = true;
    } else if (state.isClosed()) {
      facebookLinkActive = false;
    }
  }
}

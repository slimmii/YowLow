package com.pimpbunnies.yowlow;

import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.pimpbunnies.yowlow.R;
import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Guest;


public class MainActivity extends FacebookActivity {
  private int fCounter = 0;
  private TextView fRandomAction;
  private ImageView fGuestImage;
  private TextView fGuestName;
  private String[] fActions = new String[] { "Pintje", "Pintje", "Pintje", "Pintje", "Pintje", "Blauwe Smurf", "Blauwe Smurf", "Blauwe Smurf", "Rode Wodka/Sprite","Rode wodka Rodeo","Pisang Orange","Jenever"};
  private boolean fShuffleing = false;

  public void onRandomButtonClicked(View view) {
    BirthdaySQLiteHelper db = new BirthdaySQLiteHelper(MainActivity.this);
    final List<Guest> guests = db.getAllGuests();
    final Random rand = new Random();
    final int max = guests.size();


    final Handler handler = new Handler();
    fShuffleing = true;
    handler.postDelayed(new Runnable() {
      public void run() {
        int randomNum = rand.nextInt(max);
        int randomAction = rand.nextInt(fActions.length);
        Guest guest = guests.get(randomNum);   
        Bitmap bitmap = BitmapFactory.decodeByteArray(guest.getPicture(), 0, guest.getPicture().length);

        fRandomAction.setText(fActions[randomAction]);
        fGuestImage.setImageBitmap(bitmap);
        fGuestName.setText(guest.getName());
        if (fCounter++ >= 60) {
          fCounter = 0;
          fShuffleing = false;
        } else {
          handler.postDelayed(this, 5 + (fCounter * 5));
        }
      }
    }, 0);
    db.close();
  }
  
  public void onGuestClicked(View view) {
    if (fShuffleing) {
      Toast.makeText(this, "Everyday I'm shuffleing!", Toast.LENGTH_SHORT).show();
    } else {
      new PictureClickedDialog().show(getSupportFragmentManager(),"");
      
    }
  }

  public void onImportButtonClicked(View view) {
    Intent intent = new Intent(this, ImportActivity.class);

    startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    fGuestImage = (ImageView) findViewById(R.id.activity_main_guest_image);
    fGuestName = (TextView) findViewById(R.id.activity_main_guest_name);
    fRandomAction = (TextView) findViewById(R.id.activity_main_random_action);
  }
}

package com.pimpbunnies.yowlow.databse;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pimpbunnies.yowlow.model.Guest;

public class BirthdaySQLiteHelper extends SQLiteOpenHelper {

  public BirthdaySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // All Static variables
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "alcoholizer";

  // Contacts table name
  private static final String TABLE_GUESTS = "guests";

  // Contacts Table Columns names
  private static final String KEY_ID = "id";
  private static final String KEY_NAME = "name";
  private static final String KEY_PICTURE = "picture";
  private static final String KEY_SELECTED = "selected";

  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_GUESTS + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, "
        + KEY_PICTURE + " BLOB, "
        + KEY_SELECTED + " INTEGER" 
        + ")";
    db.execSQL(CREATE_CONTACTS_TABLE);
  }
  
  public void flush() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_GUESTS + ";");
    db.close();
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESTS);
    // Create tables again
    onCreate(db);
  }

  // Adding new Guest
  public void addGuest(Guest guest) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(KEY_NAME, guest.getName()); // Guest Name
    values.put(KEY_PICTURE, guest.getPicture()); // Guest Picture
    values.put(KEY_SELECTED, (guest.isSelected()) ? 1 : 0); // Guest Picture
    
    // Inserting Row
    db.insert(TABLE_GUESTS, null, values);
    db.close(); // Closing database connection
  }

  // Getting single Guest
  public Guest getGuest(int id) {
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(TABLE_GUESTS, new String[] { KEY_ID,
        KEY_NAME, KEY_PICTURE, KEY_SELECTED }, KEY_ID + "=?",
        new String[] { String.valueOf(id) }, null, null, null, null);
    if (cursor != null)
      cursor.moveToFirst();
    Guest contact = new Guest(Integer.parseInt(cursor.getString(0)),
        cursor.getString(1), cursor.getInt(2)!=0);
    db.close();
    return contact;
  }

  public int getGuestCount() {
    String countQuery = "SELECT  * FROM " + TABLE_GUESTS;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);
    cursor.close();
    db.close();
    // return count
    return cursor.getCount();
  }

  public List<Guest> getAllGuests() {
    List<Guest> guestList = new ArrayList<Guest>();
    // Select All Query
    String selectQuery = "SELECT  * FROM " + TABLE_GUESTS;

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
      do {
        Guest guest = new Guest();
        guest.setId(Integer.parseInt(cursor.getString(0)));
        guest.setName(cursor.getString(1));
        guest.setPicture(cursor.getBlob(2));
        guest.setSelected(cursor.getInt(3)!=0);
        // Adding contact to list
        guestList.add(guest);
      } while (cursor.moveToNext());
    }

    db.close();
    
    // return contact list
    return guestList;
  }
}

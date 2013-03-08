package com.pimpbunnies.yowlow.databse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;

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
	private static final String TABLE_IMAGES = "images";
	private static final String TABLE_GROUPS = "groups";
	private static final String TABLE_IMAGES_GROUPS = "images_groups";

	// Contacts Table Columns names
	private static final String KEY_IMAGE_ID = "id";
	private static final String KEY_IMAGE_NAME = "name";
	private static final String KEY_IMAGE_PICTURE = "picture";
	private static final String KEY_IMAGE_PICTURE_SOURCE = "picture_source";

	private static final String KEY_GROUP_ID = "id";
	private static final String KEY_GROUP_NAME = "name";

	private static final String KEY_IMAGE_FOREIGN_ID = "image_id";
	private static final String KEY_GROUP_FOREIGN_ID = "group_id";

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
				+ KEY_IMAGE_ID + " INTEGER PRIMARY KEY,"
				+ KEY_IMAGE_NAME + " TEXT, " + KEY_IMAGE_PICTURE + " BLOB, "
				+ KEY_IMAGE_PICTURE_SOURCE + " TEXT" + ")";
		String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
				+ KEY_GROUP_ID + " INTEGER PRIMARY KEY,"
				+ KEY_GROUP_NAME + " TEXT" + ")";
		String CREATE_IMAGE_GROUP_TABLE = "CREATE TABLE " + TABLE_IMAGES_GROUPS
				+ "(" + KEY_IMAGE_FOREIGN_ID + " INTEGER,"
				+ KEY_GROUP_FOREIGN_ID + " INTEGER" + ")";

		db.execSQL(CREATE_IMAGE_TABLE);
		db.execSQL(CREATE_GROUP_TABLE);
		db.execSQL(CREATE_IMAGE_GROUP_TABLE);
	}

	public void flush() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_IMAGES + ";");
		db.execSQL("DELETE FROM " + TABLE_GROUPS + ";");
		db.execSQL("DELETE FROM " + TABLE_IMAGES_GROUPS + ";");
		
		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_ID, 0);
		values.put(KEY_GROUP_NAME, "Default");

		// Inserting Row
		db.insert(TABLE_GROUPS, null, values);		
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES_GROUPS);
		// Create tables again
		onCreate(db);
	}

	public void createImage(Image image) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IMAGE_ID, image.getId());
		values.put(KEY_IMAGE_NAME, image.getName());
		values.put(KEY_IMAGE_PICTURE, image.getPicture());
		values.put(KEY_IMAGE_PICTURE_SOURCE, image.getPictureSource());

		// Inserting Row
		db.insert(TABLE_IMAGES, null, values);
		db.close(); // Closing database connection
	}

	public void createGroup(Group group) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_ID, group.getId());
		values.put(KEY_GROUP_NAME, group.getName());

		// Inserting Row
		db.insert(TABLE_GROUPS, null, values);
		db.close(); // Closing database connection
	}

	public int getImageCount() {
		String countQuery = "SELECT  * FROM " + TABLE_IMAGES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();
		// return count
		return cursor.getCount();
	}
	
	public void addImageToGroup(Image image, Group group) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_IMAGE_FOREIGN_ID, image.getId());
		values.put(KEY_GROUP_FOREIGN_ID, group.getId());

		db.insert(TABLE_IMAGES_GROUPS, null, values);
		db.close(); // Closing database connection
	}
	
	public void removeImageFromGroup(Image image, Group group) {
		SQLiteDatabase db = this.getWritableDatabase();
		String[] ids = {String.valueOf(image.getId()), String.valueOf(group.getId())};
		db.delete("rows" , KEY_IMAGE_FOREIGN_ID + "=? AND " + KEY_GROUP_FOREIGN_ID +"=?" , ids );
		db.close();
	}

	public List<Group> getGroups() {
		List<Group> groupList = new ArrayList<Group>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_GROUPS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Group group = new Group();
				group.setId(Integer.parseInt(cursor.getString(0)));
				group.setName(cursor.getString(1));
				groupList.add(group);
			} while (cursor.moveToNext());
		}

		db.close();
		return groupList;
	}
	
	public List<Image> getImages() {
		List<Image> imageList = new ArrayList<Image>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Image image = new Image();
				image.setId(Integer.parseInt(cursor.getString(0)));
				image.setName(cursor.getString(1));
				image.setPicture(cursor.getBlob(2));
				image.setPictureSource(cursor.getString(3));
				imageList.add(image);
			} while (cursor.moveToNext());
		}

		db.close();

		// return contact list
		return imageList;
	}
	
	public List<Image> getImages(Group group) {
		List<Image> imageList = new ArrayList<Image>();
		String selectQuery ="SELECT i.* " +
		"FROM " + TABLE_IMAGES + " i " +
		"JOIN " + TABLE_IMAGES_GROUPS + " ig ON i." + KEY_IMAGE_ID + " = ig." + KEY_IMAGE_FOREIGN_ID + " " +
		"JOIN " + TABLE_GROUPS + " g ON g." + KEY_GROUP_ID + "=ig." + KEY_GROUP_FOREIGN_ID + " " +
		"WHERE g.id=?";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] {	String.valueOf(group.getId())});

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Image image = new Image();
				image.setId(Integer.parseInt(cursor.getString(0)));
				image.setName(cursor.getString(1));
				image.setPicture(cursor.getBlob(2));
				image.setPictureSource(cursor.getString(3));
				// Adding contact to list
				imageList.add(image);
			} while (cursor.moveToNext());
		}

		db.close();
		return imageList;
	}
}

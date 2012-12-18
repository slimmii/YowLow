package com.pimpbunnies.birthday.model;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Guest {
  private int fId;
  private String fName;
  private byte[] fPicture;
  
  public Guest() {
    
  }
  
  public Guest(int id, String name) {
    this.fId = id;
    this.fName = name;
  }
  
  public Guest(int id, String name, byte[] picture) {
    this(id, name);
    this.fPicture = picture;
  }
  
  public Guest(int id, String name, Bitmap picture) {
    this(id, name);
    this.fPicture = getBitmapAsByteArray(picture);
  }

  public byte[] getPicture() {
    return fPicture;
  }

  public void setPicture(byte[] fPicture) {
    this.fPicture = fPicture;
  }

  public int getId() {
    return fId;
  }

  public void setId(int id) {
    this.fId = id;
  }

  public String getName() {
    return fName;
  }

  public void setName(String name) {
    this.fName = name;
  }
  
  private byte[] getBitmapAsByteArray(Bitmap bitmap) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    bitmap.compress(CompressFormat.PNG, 0, outputStream);
    return outputStream.toByteArray();
 } 
  
  
  
  
}

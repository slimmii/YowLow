package com.pimpbunnies.yowlow.model;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Guest {
	private int fId;
	private String fName;
	private byte[] fPicture;
	private boolean fSelected;
	private String mPictureSource;

	public Guest() {

	}

	public Guest(int id, String name, String pictureSource, boolean selected) {
		this.fId = id;
		this.fName = name;
		this.mPictureSource = pictureSource;
		this.fSelected = selected;
	}

	public Guest(int id, String name, String pictureSource, boolean selected, byte[] picture) {
		this(id, name, pictureSource, selected);
		this.fPicture = picture;
	}

	public Guest(int id, String name, String pictureSource, boolean selected, Bitmap picture) {
		this(id, name, pictureSource, selected);
		this.fPicture = getBitmapAsByteArray(picture);
	}

	public byte[] getPicture() {

		return fPicture;
	}

	public void setPicture(byte[] fPicture) {
		this.fPicture = fPicture;
	}


	public boolean isSelected() {
		return fSelected;
	}

	public void setSelected(boolean selected) {
		this.fSelected = selected;
	}



	public void setPicture(Bitmap picture) {
		this.fPicture = getBitmapAsByteArray(picture);
		picture.recycle(); // Recycle the Bitmap, we use the byte array instead.
		System.out.println(fPicture.length);
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

	public String getPictureSource() {
		return mPictureSource;
	}

	public void setPictureSource(String pictureSource) {
		this.mPictureSource = pictureSource;
	}
	
	private byte[] getBitmapAsByteArray(Bitmap bitmap) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.PNG, 0, outputStream);
		return outputStream.toByteArray();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fName == null) ? 0 : fName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Guest other = (Guest) obj;
		if (fName == null) {
			if (other.fName != null)
				return false;
		} else if (!fName.equals(other.fName))
			return false;
		return true;
	} 

	@Override
	public String toString() {
		return fName;
	}


}

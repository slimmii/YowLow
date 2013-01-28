package com.pimpbunnies.yowlow.model;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Guest {
	private int fId;
	private String fName;
	private byte[] fPicture;
	private boolean fSelected;

	public Guest() {

	}

	public Guest(int id, String name, boolean selected) {
		this.fId = id;
		this.fName = name;
		this.fSelected = selected;
	}

	public Guest(int id, String name, boolean selected, byte[] picture) {
		this(id, name, selected);
		this.fPicture = picture;
	}

	public Guest(int id, String name, boolean selected, Bitmap picture) {
		this(id, name, selected);
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



	public void setPicture(Bitmap fPicture) {
		this.fPicture = getBitmapAsByteArray(fPicture);
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

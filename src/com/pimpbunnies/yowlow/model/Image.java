package com.pimpbunnies.yowlow.model;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Image {
	private static int CURRENT_INDEX = 0;
	private int id;
	private String name;
	private byte[] picture;
	private String pictureSource;

	public Image() {

	}

	public Image(String name, String pictureSource) {
		this.id = CURRENT_INDEX++;
		this.name = name;
		this.pictureSource = pictureSource;
	}

	public Image(String name, String pictureSource, byte[] picture) {
		this(name, pictureSource);
		this.picture = picture;
	}

	public Image(String name, String pictureSource, Bitmap picture) {
		this(name, pictureSource);
		this.picture = getBitmapAsByteArray(picture);
	}

	public byte[] getPicture() {

		return picture;
	}

	public void setPicture(byte[] fPicture) {
		this.picture = fPicture;
	}

	public void setPictureBitmap(Bitmap pic) {
		this.picture = getBitmapAsByteArray(pic);
		pic.recycle(); // Recycle the Bitmap, we use the byte array instead.
		System.out.println(picture.length);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureSource() {
		return pictureSource;
	}

	public void setPictureSource(String pictureSource) {
		this.pictureSource = pictureSource;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Image other = (Image) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	} 

	@Override
	public String toString() {
		return name;
	}


}
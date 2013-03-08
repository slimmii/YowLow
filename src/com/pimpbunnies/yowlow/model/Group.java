package com.pimpbunnies.yowlow.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Group {
	private static int CURRENT_INDEX = 0;
	private int mId;
	private String mName;
	
	public Group() {
	}
	
	public Group(int id, String name) {
		this();
		this.mId = id;
		this.mName = name;
	}	
	
	public Group(String name) {
		this(CURRENT_INDEX++, name);
	}
	
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		this.mId = id;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		this.mName = name;
	}	
	
	@Override
	public String toString() {
		return mName;
	}
}

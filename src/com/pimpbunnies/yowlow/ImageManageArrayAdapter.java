package com.pimpbunnies.yowlow;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;

public class ImageManageArrayAdapter extends ArrayAdapter<Group> {
	  private final Activity context;
	  private final List<Group> groups;
	  private BirthdaySQLiteHelper mDb;

	  static class ViewHolder {
	    public TextView text;
	    public GridLayout grid;
	  }

	  public ImageManageArrayAdapter(Activity context, List<Group> groups, BirthdaySQLiteHelper db) {
	    super(context, R.layout.imagemanage_list_item, groups);
	    this.context = context;
	    this.groups = groups;
	    this.mDb = db;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    if (rowView == null) {
	      LayoutInflater inflater = context.getLayoutInflater();
	      rowView = inflater.inflate(R.layout.imagemanage_list_item, null);
	      ViewHolder viewHolder = new ViewHolder();
	      viewHolder.text = (TextView) rowView.findViewById(R.id.imagemanage_groupname);
	      viewHolder.grid = (GridLayout) rowView.findViewById(R.id.imagemanage_grid);
	      rowView.setTag(viewHolder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    Group g = groups.get(position);
	    holder.text.setText(g.getName());
	    holder.grid.removeAllViews();
	    List<Image> images = mDb.getImages(g);
	    for (Image image : images) {
	    	ImageView iv = new ImageView(context);
			Bitmap bitmap = BitmapFactory.decodeByteArray(image.getPicture(), 0,
					image.getPicture().length);	    	
	    	iv.setImageBitmap(bitmap);
	    	holder.grid.addView(iv);
	    }

	    return rowView;
	  }
	} 
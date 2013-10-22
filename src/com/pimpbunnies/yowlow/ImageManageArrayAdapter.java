package com.pimpbunnies.yowlow;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pimpbunnies.yowlow.databse.BirthdaySQLiteHelper;
import com.pimpbunnies.yowlow.model.Group;
import com.pimpbunnies.yowlow.model.Image;

public class ImageManageArrayAdapter extends ArrayAdapter<Group> {
	  private final ImageManageActivity context;
	  private final List<Group> groups;
	  private BirthdaySQLiteHelper mDb;

	  static class ViewHolder {
	    public TextView text;
	    public GridLayout grid;
	    public ImageButton deletebutton;
	  }

	  public ImageManageArrayAdapter(ImageManageActivity context, List<Group> groups, BirthdaySQLiteHelper db) {
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
	      viewHolder.deletebutton = (ImageButton) rowView.findViewById(R.id.imagemanage_deletebutton);
	      rowView.setTag(viewHolder);
	    }  

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    final Group g = groups.get(position);
	    holder.text.setText(g.getName());
	    holder.grid.removeAllViews();
	    holder.deletebutton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				context.removeGroup(g);
				return true;
			}
		});
	    holder.deletebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				context.removeImageFromGroup(g);
			}
		});
	    rowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				context.addImageToGroup(g);
				
			}
		});	  	    

	    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();

	    
	    holder.grid.setColumnCount(10);
	    List<Image> images = mDb.getImages(g);
	    for (Image image : images) {
	    	ImageView iv = new ImageView(context);
			Bitmap bitmap = BitmapFactory.decodeByteArray(image.getPicture(), 0,
					image.getPicture().length);	  
			holder.grid.setColumnCount((width / bitmap.getWidth())-1);
	    	iv.setImageBitmap(bitmap);
	    	holder.grid.addView(iv);
	    }

	    return rowView;
	  }
	} 
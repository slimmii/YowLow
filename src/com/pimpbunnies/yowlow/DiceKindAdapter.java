package com.pimpbunnies.yowlow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimpbunnies.yowlow.model.Guest;

public class DiceKindAdapter extends BaseAdapter {
	/** Remember our context so we can use it when constructing views. */
	private Context mContext;
	 
	/**
	* Hold onto a copy of the entire Contact List.
	*/
	private List<DiceKind> mItems = new ArrayList<DiceKind>();
	
	public DiceKindAdapter(Context context, ArrayList<DiceKind> kinds) {
		mContext = context;
		mItems = kinds;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder{
		public TextView dicekind_list_item_name;
		public TextView dicekind_list_item_description;
		public ImageView dicekind_list_item_image;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi =
					(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.dicekind_list_item, null);
			holder = new ViewHolder();
			holder.dicekind_list_item_name = (TextView) v.findViewById(R.id.dicekind_list_item_name);
			holder.dicekind_list_item_description = (TextView) v.findViewById(R.id.dicekind_list_item_description);
			holder.dicekind_list_item_image = (ImageView) v.findViewById(R.id.dicekind_list_item_image);
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		final DiceKind kind = (DiceKind) getItem(position);
		if (kind != null) {
			holder.dicekind_list_item_name.setText(kind.getName());
			holder.dicekind_list_item_description.setText(kind.getDescription());
			holder.dicekind_list_item_image.setImageDrawable(kind.getIcon());
		}

		return v;
	}

}

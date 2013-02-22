package com.pimpbunnies.yowlow;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimpbunnies.yowlow.model.Guest;

public class GuestAdapter extends ArrayAdapter<Guest> implements Filterable {

	private Context fContext;
	private List<Guest> fOrigionalValues;
	private List<Guest> fObjects;
	private Filter fFilter;
	private String mFilterString;

	public String getFilterString() {
		return mFilterString;
	}

	public void setFilterString(String filterString) {
		this.mFilterString = filterString;
	}

	public GuestAdapter(Context context, int viewResourceId,
			List<Guest> guests) {
		super(context, viewResourceId, guests);
		fContext = context;
		fOrigionalValues = guests;
		fObjects = new ArrayList<Guest>(guests);
	}

	public static class ViewHolder{
		public TextView guest_list_item_name;
		public ImageView guest_list_item_image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi =
					(LayoutInflater) fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.guest_list_item, null);
			holder = new ViewHolder();
			holder.guest_list_item_name = (TextView) v.findViewById(R.id.guest_list_item_name);
			holder.guest_list_item_image = (ImageView) v.findViewById(R.id.guest_list_item_image);
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		final Guest guest = getItem(position);
		if (guest != null) {
			holder.guest_list_item_name.setText(guest.getName());
			byte[] byteArray = guest.getPicture();
			Bitmap bitmap = BitmapFactory.decodeByteArray(guest.getPicture() , 0, guest.getPicture() .length);
			holder.guest_list_item_image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 64,64, false));

		}
		if (guest.isSelected()) {
			v.setBackgroundColor(Color.rgb(188, 217, 255));
		} else {
			v.setBackgroundColor(Color.WHITE);
		}
		return v;
	}
	
	@Override
	public void remove(Guest object) {
		fOrigionalValues.remove(object);
		this.notifyDataSetChanged();
	}
	@Override
	public void add(Guest object) {
		fOrigionalValues.add(object);
		this.notifyDataSetChanged();
	}
	
	public void addAll(List<Guest> guests) {
		for (Guest guest : guests) {
			fOrigionalValues.add(guest);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return fObjects.size();
	}

	@Override
	public Guest getItem(int position) {
		return fObjects.get(position);
	}

	public Filter getFilter() {
		if (fFilter == null) {
			fFilter = new CustomFilter();
		}
		return fFilter;
	}
	
	public void filter(String string) {
		mFilterString = string;
		filter();
	}
	
	public void filter() {
		getFilter().filter(mFilterString);
	}

	private class CustomFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if(constraint == null || constraint.length() == 0) {
				ArrayList<Guest> list = new ArrayList<Guest>(fOrigionalValues);
				results.values = list;
				results.count = list.size();
			} else {
				String filter = constraint.toString().toLowerCase();
				ArrayList<Guest> newValues = new ArrayList<Guest>();
				for(int i = 0; i < fOrigionalValues.size(); i++) {
					Guest item = fOrigionalValues.get(i);
					String[] parts = item.getName().split(" ");
					for (int j=0;j<parts.length;j++) {
						if(parts[j].toLowerCase().startsWith(filter)) {
							newValues.add(item);
						}
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}       

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			fObjects = (List<Guest>) results.values;
			Log.d("CustomArrayAdapter", String.valueOf(results.values));
			Log.d("CustomArrayAdapter", String.valueOf(results.count));
			notifyDataSetChanged();
		}

	}

}

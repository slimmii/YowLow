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

import com.facebook.android.Facebook;
import com.pimpbunnies.yowlow.model.Image;

public class GuestAdapter extends ArrayAdapter<Image> implements Filterable {

	private Context fContext;
	private List<Image> fOrigionalValues;
	private List<Image> fObjects;
	private Filter fFilter;
	private String mFilterString = "";
	private String mFilterSource = "";

	public String getFilterString() {
		return mFilterString;
	}

	public void setFilterString(String filterString) {
		this.mFilterString = filterString;
	}

	public void setFilterSource(String filterSource) {
		this.mFilterSource = filterSource;
	}

	public String getFilterSource() {
		return mFilterSource;
	}

	public GuestAdapter(Context context, int viewResourceId, List<Image> guests) {
		super(context, viewResourceId, guests);
		fContext = context;
		fOrigionalValues = guests;
		fObjects = new ArrayList<Image>(guests);
	}

	public static class ViewHolder {
		public TextView guest_list_item_name;
		public ImageView guest_list_item_image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) fContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.guest_list_item, null);
			holder = new ViewHolder();
			holder.guest_list_item_name = (TextView) v
					.findViewById(R.id.guest_list_item_name);
			holder.guest_list_item_image = (ImageView) v
					.findViewById(R.id.guest_list_item_image);
			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final Image guest = getItem(position);
		if (guest != null) {
			holder.guest_list_item_name.setText(guest.getName());
			byte[] byteArray = guest.getPicture();

			if (byteArray != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
						byteArray.length);
				holder.guest_list_item_image.setImageBitmap(bitmap);
			} else {
				holder.guest_list_item_image
						.setImageResource(R.drawable.ic_missing_person);
			}

		}
		return v;
	}

	@Override
	public void remove(Image object) {
		fOrigionalValues.remove(object);
		this.notifyDataSetChanged();
	}

	@Override
	public void add(Image object) {
		fOrigionalValues.add(object);
		this.notifyDataSetChanged();
	}

	public void addAll(List<Image> guests) {
		for (Image guest : guests) {
			fOrigionalValues.add(guest);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return fObjects.size();
	}

	@Override
	public Image getItem(int position) {
		return fObjects.get(position);
	}

	public Filter getFilter() {
		if (fFilter == null) {
			fFilter = new CustomFilter();
		}
		return fFilter;
	}

	public void filter() {
		getFilter().filter(mFilterString + " @@@ " + mFilterSource);
	}

	private class CustomFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults results = new FilterResults();

			if (mFilterString == null) {
				mFilterString = "";
			}

			String filter = mFilterString.toString().toLowerCase();
			ArrayList<Image> newValues = new ArrayList<Image>();
			for (int i = 0; i < fOrigionalValues.size(); i++) {
				Image item = fOrigionalValues.get(i);
				String[] parts = item.getName().split(" ");
				if (!item.getPictureSource().startsWith(
						mFilterSource)) {

				} else {			
					for (int j = 0; j < parts.length; j++) {
						if (parts[j].toLowerCase().startsWith(filter)) {
							newValues.add(item);
							break;
						}
					}
				}
			}
			results.values = newValues;
			results.count = newValues.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			fObjects = (List<Image>) results.values;
			Log.d("CustomArrayAdapter", String.valueOf(results.values));
			Log.d("CustomArrayAdapter", String.valueOf(results.count));
			notifyDataSetChanged();
		}

	}

}

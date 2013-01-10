package com.pimpbunnies.yowlow;
import java.util.List;

import com.pimpbunnies.yowlow.model.Guest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GuestAdapter extends ArrayAdapter<Guest> {

  private Context fContext;
  private List<Guest> fGuests;
  
  public GuestAdapter(Context context, int viewResourceId,
      List<Guest> guests) {
    super(context, viewResourceId, guests);
    fContext = context;
    fGuests = guests;
  }
  
  public static class ViewHolder{
    public TextView guest_list_item_name;
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
        v.setTag(holder);
    }
    else
        holder=(ViewHolder)v.getTag();

    final Guest guest = fGuests.get(position);
    if (guest != null) {
        holder.guest_list_item_name.setText(guest.getName());
    }
    return v;
  }

}
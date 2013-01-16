package com.pimpbunnies.yowlow;
import java.util.List;

import com.pimpbunnies.yowlow.model.Guest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    final Guest guest = fGuests.get(position);
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

}

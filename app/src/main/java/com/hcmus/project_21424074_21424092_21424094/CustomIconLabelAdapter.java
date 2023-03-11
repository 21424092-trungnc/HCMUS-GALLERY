package com.hcmus.project_21424074_21424092_21424094;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomIconLabelAdapter extends ArrayAdapter<String> {
    Context context; Integer[] thumbnails; String[] items;
    public CustomIconLabelAdapter(Context context, int layoutToBeInflated, String[] items, Integer[] thumbnails) {
        super(context, R.layout.custom_row_icon_label, items);
        this.context = context;
        this.thumbnails = thumbnails;
        this.items = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (( Activity ) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_row_icon_label, null);
        TextView label = ( TextView ) row.findViewById(R.id.lable);
        TextView mssvitem = ( TextView ) row.findViewById(R.id.mssvitem);
        ImageView icon = ( ImageView ) row.findViewById(R.id.icon);
        String[] str = items[position].split("-");
        label.setText(str[0]);
        mssvitem.setText(str[1]);
        icon.setImageResource(thumbnails[position]);
        return (row);
    }
}

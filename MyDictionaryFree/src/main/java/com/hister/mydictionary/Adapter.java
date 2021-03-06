package com.hister.mydictionary;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.hister.mydictionary.Custom;
import com.hister.mydictionary.R;

public class Adapter extends ArrayAdapter<CustomShow>{
    private ArrayList<CustomShow> entries;
    private Activity activity;

    public Adapter(Activity a, int textViewResourceId, ArrayList<CustomShow> entries) {
        super(a, textViewResourceId, entries);
        this.entries = entries;
        this.activity = a;
    }

    public static class ViewHolder{
        public TextView item1;
        public TextView item2;
        public CheckBox item3;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);

            holder = new ViewHolder();
            holder.item1 = (TextView) v.findViewById(R.id.text1);
            holder.item2 = (TextView) v.findViewById(R.id.text2);
            holder.item3 = (CheckBox) v.findViewById(R.id.checkBox);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();

        final CustomShow custom = entries.get(position);
        if (custom != null) {
            holder.item1.setText(custom.getWord());
            String meaning = custom.getMeaning();
            if (meaning.length() > 40) {
                meaning = meaning.substring(0, 40);
                meaning+= "...";
            }
            holder.item2.setText(meaning);
            holder.item2.setVisibility(custom.isMeaningVisible() ? View.VISIBLE : View.INVISIBLE);
            holder.item3.setChecked(custom.isChChecked());
            holder.item3.setVisibility(custom.isChVisible() ? View.VISIBLE : View.INVISIBLE);
        }
        return v;
    }
}
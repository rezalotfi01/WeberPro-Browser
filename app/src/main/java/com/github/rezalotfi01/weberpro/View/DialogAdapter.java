package com.github.rezalotfi01.weberpro.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.rezalotfi01.weberpro.R;

import java.util.List;

public class DialogAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final int layoutResId;
    private final List<String> list;

    public DialogAdapter(Context context, int layoutResId, List<String> list) {
        super(context, layoutResId, list);
        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    private static class Holder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
            holder = new Holder();
            holder.textView = view.findViewById(R.id.dialog_text_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.textView.setText(list.get(position));

        return view;
    }
}

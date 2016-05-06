package com.example.sean.dabull;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

public class SpecialAdapter extends SimpleCursorAdapter {
    private int[] colors = new int[] { Color.WHITE, Color.argb(180, 240, 210, 181)};

    public SpecialAdapter(Context context, int resource, MatrixCursor mc, String[] from, int[] to, int d) {
        super(context, resource, mc, from, to, d);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}
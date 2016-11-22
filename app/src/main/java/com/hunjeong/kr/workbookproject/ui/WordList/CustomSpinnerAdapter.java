package com.hunjeong.kr.workbookproject.ui.WordList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunjeong.kr.workbookproject.R;

/**
 * Created by JeongHun on 2016. 11. 22..
 */

public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    String[] strings;

    public CustomSpinnerAdapter(Context context, String[] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int i) {
        return strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.row_spinner, viewGroup, false);
        }
        ((TextView)v.findViewById(R.id.text_spinner)).setText(strings[i]);
        return v;
    }
}

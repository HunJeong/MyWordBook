package com.hunjeong.kr.workbookproject.ui.SwipeExam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunjeong.kr.workbookproject.R;
import com.hunjeong.kr.workbookproject.model.Word;

import java.util.LinkedList;

/**
 * Created by JeongHun on 2016. 11. 24..
 */

public class SwipeStackAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Word> list;
    private boolean mean;

    public SwipeStackAdapter(Context context, LinkedList<Word> list, boolean mean) {
        this.context = context;
        this.list = list;
        this.mean = mean;
    }


    public void setList(LinkedList<Word> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.view_swipe_exam, viewGroup, false);
        }
        if (mean) {
            ((TextView)v.findViewById(R.id.swipe_exam_up)).setText(list.get(i).getWord());
            ((TextView)v.findViewById(R.id.swipe_exam_down)).setText(list.get(i).getMean());
        } else {
            ((TextView)v.findViewById(R.id.swipe_exam_up)).setText(list.get(i).getMean());
            ((TextView)v.findViewById(R.id.swipe_exam_down)).setText(list.get(i).getWord());
        }
        ((TextView)v.findViewById(R.id.page_number)).setText(i+1 + " / " + list.size());
        return v;
    }
}

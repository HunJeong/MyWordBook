package com.hunjeong.kr.workbookproject.ui;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.gordonwong.materialsheetfab.AnimatedFab;

/**
 * Created by JeongHun on 2016. 11. 15..
 */

public class Fab extends FloatingActionButton implements AnimatedFab {

    public Fab(Context context) {
        super(context);
    }

    public Fab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void show(float translationX, float translationY) {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(View.INVISIBLE);
    }
}

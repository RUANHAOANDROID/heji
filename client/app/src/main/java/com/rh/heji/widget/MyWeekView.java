package com.rh.heji.widget;

import android.content.Context;
import android.graphics.Canvas;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

public class MyWeekView extends WeekView {

    public MyWeekView(Context context) {
        super(context);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {

    }
}

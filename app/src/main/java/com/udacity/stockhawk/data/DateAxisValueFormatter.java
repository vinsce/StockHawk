package com.udacity.stockhawk.data;

import android.content.Context;
import android.text.format.DateUtils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;

/**
 * Created by vinsce on 22/01/17 at 18.41.
 *
 * @author vinsce
 */

public class DateAxisValueFormatter implements IAxisValueFormatter {
    private Context mContext;

    public DateAxisValueFormatter(Context context) {
        this.mContext = context;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long millis = (long) value;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return DateUtils.formatDateTime(mContext, millis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
    }
}
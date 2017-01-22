package com.udacity.stockhawk.ui;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.udacity.stockhawk.R;

/**
 * Created by vinsce on 22/01/17 at 21.24.
 *
 * @author vinsce
 */

public class HistoryMarkerView extends MarkerView {

    private TextView mValueView, mDateView;

    public HistoryMarkerView(Context context) {
        super(context, R.layout.graph_marker_view);
        mValueView = (TextView) findViewById(R.id.value);
        mDateView = (TextView) findViewById(R.id.date);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mValueView.setText(String.valueOf(e.getY()));
        mDateView.setText(String.valueOf(e.getData()));
        super.refreshContent(e, highlight);
    }
}
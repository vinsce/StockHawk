package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.HistoryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ID_DETAIL_LOADER = 1657;

    private LineChart mLineChart;
    private String mSymbol;
    private LineDataSet mLineDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSymbol = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (mSymbol == null) throw new NullPointerException("Symbol for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        mLineChart = (LineChart) findViewById(R.id.chart);
        mLineDataSet = new LineDataSet(new ArrayList<Entry>(), "");
        styleGraph();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                Uri uri = Contract.Quote.URI.buildUpon().appendPath(mSymbol).build();
                return new CursorLoader(this, uri, null, null, null, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean cursorHasValidData = false;
        if (cursor != null && cursor.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        String name = cursor.getString(Contract.Quote.POSITION_NAME);
        Log.d(DetailsActivity.class.getSimpleName(), symbol);
        setTitle(name);

        String historyString = cursor.getString(Contract.Quote.POSITION_HISTORY);
        List<Pair<Date, Float>> history = HistoryUtils.parseHistoryFromString(historyString);
        Log.d(DetailsActivity.class.getSimpleName(), historyString);

        Collections.sort(history, new Comparator<Pair<Date, Float>>() {
            @Override
            public int compare(Pair<Date, Float> o1, Pair<Date, Float> o2) {
                return o1.first.compareTo(o2.first);
            }
        });

        mLineDataSet.clear();
        for (int i = 0; i < history.size(); i++) {
            Pair<Date, Float> data = history.get(i);
            mLineDataSet.addEntry(new Entry(i, (float) data.second, DateUtils.formatDateTime(this, data.first.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL)));
        }
        mLineDataSet.notifyDataSetChanged();

        LineData lineData = new LineData(mLineDataSet);
        mLineChart.setData(lineData);
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @SuppressWarnings("deprecation")
    private void styleGraph() {
        mLineDataSet.setMode(LineDataSet.Mode.LINEAR);
        mLineDataSet.setFillAlpha(100);
        mLineDataSet.setDrawFilled(true);
        mLineDataSet.setFillColor(getResources().getColor(R.color.colorAccent));
        mLineDataSet.setColor(getResources().getColor(R.color.colorAccent));
        mLineDataSet.setDrawCircles(false);
        mLineDataSet.setLineWidth(2);
        mLineDataSet.setHighLightColor(getResources().getColor(R.color.colorPrimary));

        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setDescription(null);
        mLineChart.setScaleEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setMarker(new HistoryMarkerView(this));

        YAxis yAxis = mLineChart.getAxisLeft();
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setEnabled(false);
        yAxis.setEnabled(false);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        mLineChart.invalidate();
    }
}

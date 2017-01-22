package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.HistoryUtils;

import java.util.Date;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ID_DETAIL_LOADER = 1657;

    private LineChart mLineChart;
    private String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSymbol = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (mSymbol == null) throw new NullPointerException("Symbol for DetailActivity cannot be null");

        mLineChart = (LineChart) findViewById(R.id.chart);
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
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
        List<Pair<Date, Double>> history = HistoryUtils.parseHistoryFromString(historyString);
        Log.d(DetailsActivity.class.getSimpleName(), historyString);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

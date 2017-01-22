package com.udacity.stockhawk.ui.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by vinsce on 22/01/17 at 22.52.
 *
 * @author vinsce
 */

@SuppressWarnings("deprecation")
public class StocksWidgetRemoteViewService extends RemoteViewsService {
    public final String LOG_TAG = StocksWidgetRemoteViewService.class.getSimpleName();
    DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    public StocksWidgetRemoteViewService() {
        dollarFormatWithPlus.setPositivePrefix("+$");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                // String price = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

                if (rawAbsoluteChange > 0) {
                    views.setTextColor(R.id.change, getResources().getColor(R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.change, getResources().getColor(R.color.material_red_700));
                }
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);

                views.setTextViewText(R.id.symbol, symbol);
                //views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.change, change);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Intent.EXTRA_TEXT, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

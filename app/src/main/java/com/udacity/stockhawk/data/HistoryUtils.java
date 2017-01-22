package com.udacity.stockhawk.data;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by vinsce on 22/01/17 at 18.14.
 *
 * @author vinsce
 */

public final class HistoryUtils {
    private HistoryUtils() {
    }

    public static List<Pair<Date, Float>> parseHistoryFromString(String historyString) {
        List<Pair<Date, Float>> result = new ArrayList<>(200);
        Scanner sc = new Scanner(historyString);
        sc.useDelimiter("\n");
        String tmp, dateString, valueString;
        Date date;
        Float value;
        while (sc.hasNext()) {
            tmp = sc.next();
            dateString = tmp.substring(0, tmp.indexOf(','));
            valueString = tmp.substring(tmp.indexOf(',') + 1);
            date = new Date(Long.parseLong(dateString));
            value = Float.parseFloat(valueString);
            result.add(new Pair<>(date, value));
        }
        return result;
    }
}

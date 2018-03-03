package ru.hepolise.cellrest.Utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import ru.hepolise.cellrest.R;

import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hepolise on 17.02.2018.
 */

public class WidgetUtils {
    private static String LOG_TAG = "cellLogs";
    public static SharedPreferences getSharedPrefsByWidgetId(Context context, int id) {
        SharedPreferences shrpr;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);;
        String working_prefs = sharedPreferences.getString("loaded_prefs", "prefs_0");
        long ts = sharedPreferences.getLong("widget_id_" + Integer.toString(id), 0);
        if (working_prefs.equals("prefs_" + ts)) {
            Log.d(LOG_TAG, "Using default prefs for load vars");
            shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            Log.d(LOG_TAG, "Using prefs_" + ts +" for load vars");
            shrpr = context.getSharedPreferences("prefs_" + ts, MODE_PRIVATE);
        }
        return shrpr;
    }

    public static String getDate(long timeStamp, Boolean l, String login){

        try{
            DateFormat data = DateFormat.getDateInstance(DateFormat.SHORT);
            Locale currentLocale = Locale.getDefault();
            DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale);
            Date netDate = (new Date(timeStamp));
            if (l) {
                return "(" + login + ") " + time.format(netDate) + "\n" + data.format(netDate);
            } else {
                return time.format(netDate) + "\n" + data.format(netDate);
            }
        }
        catch(Exception ex){
            return "0";
        }
    }

    public static String plurals(Long n, String form1, String form2, String form5){
        //day days days (день дня дней)
        if (n==0) return n.toString() + " " + form5;
        n = Math.abs(n) % 100;
        Long n1 = n % 10;
        if (n > 10 && n < 20) return n.toString() + " " + form5;
        if (n1 > 1 && n1 < 5) return n.toString() + " " + form2;
        if (n1 == 1) return n.toString() + " " + form1;
        return n.toString() + " " + form5;
    }


    public static void setAllTextNull(RemoteViews widgetView) {
        //SET ALL TEXT TO NULL
        widgetView.setTextViewText(R.id.inet, "");
        widgetView.setTextViewText(R.id.calls, "");
        widgetView.setTextViewText(R.id.sms, "");
        widgetView.setTextViewText(R.id.balance, "");
        widgetView.setTextViewText(R.id.date, "");
        widgetView.setTextViewText(R.id.renew, "");
        widgetView.setTextViewText(R.id.renew_inet, "");
        //widgetView.setTextViewText(R.id.balance_inet, "");
        widgetView.setTextViewText(R.id.date_inet, "");

        widgetView.setTextViewText(R.id.inet_italic, "");
        widgetView.setTextViewText(R.id.calls_italic, "");
        widgetView.setTextViewText(R.id.sms_italic, "");
        widgetView.setTextViewText(R.id.balance_italic, "");
        widgetView.setTextViewText(R.id.date_italic, "");
        widgetView.setTextViewText(R.id.renew_italic, "");
        widgetView.setTextViewText(R.id.renew_inet_italic, "");
        //widgetView.setTextViewText(R.id.balance_inet_italic, "");
        widgetView.setTextViewText(R.id.date_inet_italic, "");

        widgetView.setTextViewText(R.id.inet_bold, "");
        widgetView.setTextViewText(R.id.calls_bold, "");
        widgetView.setTextViewText(R.id.sms_bold, "");
        widgetView.setTextViewText(R.id.balance_bold, "");
        widgetView.setTextViewText(R.id.date_bold, "");
        widgetView.setTextViewText(R.id.renew_bold, "");
        widgetView.setTextViewText(R.id.renew_inet_bold, "");
        //widgetView.setTextViewText(R.id.balance_inet_bold, "");
        widgetView.setTextViewText(R.id.date_inet_bold, "");


        //set update to null
        widgetView.setTextViewText(R.id.text_upd, "");
        widgetView.setTextViewText(R.id.text_upd_italic, "");
        widgetView.setTextViewText(R.id.text_upd_bold, "");

        //set images to null
        widgetView.setImageViewBitmap(R.id.calls_logo, null);
        widgetView.setImageViewBitmap(R.id.sms_logo, null);
        widgetView.setImageViewBitmap(R.id.inet_logo, null);

        //END SET ALL TEXT TO NULL
    }

    public static int getStringResourceByName(String aString, Context ctx) {
        String packageName = ctx.getPackageName();
        return ctx.getResources().getIdentifier(aString, "id", packageName);
    }


    public static void setIntent(RemoteViews widgetView, Context context, String res, android.app.PendingIntent pIntent) {
        // set intent to reload widget on tap

        Log.d(LOG_TAG, "setting intent");
        widgetView.setOnClickPendingIntent(getStringResourceByName("inet" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("calls" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("sms" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("balance" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("date" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("renew" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("renew_inet" + res, context), pIntent);
        widgetView.setOnClickPendingIntent(getStringResourceByName("date_inet" + res, context), pIntent);

        widgetView.setOnClickPendingIntent(R.id.inet_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms_logo, pIntent);
    }

    public static String getSize(String s) {
        //return inet on MB or GB
        String result;
        float p = Float.parseFloat(s);
        if (p > 1000 || p < -1000) {
            p = p/1024;
            result = "G";
        } else {
            result = "M";
        }
        p = p * 10;
        if (Float.toString(p).endsWith("0")) {
            Log.d(LOG_TAG, "a true " + Integer.toString(Math.round(p/10)));
            return Integer.toString(Math.round(p/10)) + result;
        } else {
            Log.d(LOG_TAG, "Received: " + Float.toString(p));
            p = Math.round(p);
            Log.d(LOG_TAG, "round: " + Float.toString(p));
            p = p / 10;
            Log.d(LOG_TAG, "division: " + Float.toString(p));
            return Float.toString(p) + result;
        }

    }

    public static void setTextSize(RemoteViews widgetView, Context context, int widgetID, String res, AppWidgetManager appWidgetManager) {
        int min_h = appWidgetManager.getAppWidgetOptions(widgetID).getInt(OPTION_APPWIDGET_MAX_HEIGHT);
        float multiplier = (float) 5;
        Float size = min_h / multiplier;
        widgetView.setFloat(getStringResourceByName("inet" + res, context), "setTextSize", size);
        widgetView.setFloat(getStringResourceByName("calls" + res, context), "setTextSize", size);
        widgetView.setFloat(getStringResourceByName("sms" + res, context), "setTextSize", size);
    }
}

package ru.hepolise.cellrest.Widgets;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.accounts.Account;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.FloatProperty;
import android.util.Log;

import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;


import ru.hepolise.cellrest.Activities.AccountChooser;
import ru.hepolise.cellrest.BuildConfig;
import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.Utils.Colorize;
import ru.hepolise.cellrest.Utils.QuickstartPreferences;
import ru.hepolise.cellrest.Utils.Utils;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH;
import static android.content.Context.MODE_PRIVATE;

public class TraffWidget extends AppWidgetProvider {
    Context contextglobal;
    android.appwidget.AppWidgetManager appWidgetManagerglobal;
    String UPD;
    String ACTION_APPWIDGET_FORCE_UPDATE = "";
    String login;
    String pass;
    String op;
    String android_id;
    String pin_code;
    String version;
    String token;
    String return_;
    String tz;


    final String LOG_TAG = "cellLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        String account = sharedPreferences.getString(Integer.toString(appWidgetIds[0]), "");

        Log.d(LOG_TAG, "account: " + account + ", widgetID: " + appWidgetIds[0]);
        if (account.equals("")) {



            SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
            UPD = shrpr.getString(QuickstartPreferences.update, "0");


            if (UPD.equals("1")) {
                Intent intent = new Intent(context, AccountChooser.class);
                intent.putExtra("id", appWidgetIds[0]);
                context.startActivity(intent);
            }



        }

        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id, context.getString(R.string.updating));

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    @Override
    public void onAppWidgetOptionsChanged (Context context,
                                           AppWidgetManager appWidgetManager,
                                           int appWidgetId,
                                           Bundle newOptions) {
        int max_w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);
//        int max_h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT);
//        int min_w = newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH);
//        int min_h = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT);
//        //make only inet for small widget
//        Toast.makeText(context, Integer.toString(max_w) + ": max_w; " + Integer.toString(max_h) + ": max_h; " +
//                Integer.toString(min_w) + ": min_w; " + Integer.toString(min_h) + ": min_h;", Toast.LENGTH_LONG).show();


        SharedPreferences sh = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        String account = sh.getString(Integer.toString(appWidgetId), "0");

        String working_prefs = sh.getString("working_prefs", "prefs_0");
        SharedPreferences shrpr;
        if (working_prefs.equals("prefs_" + account)) {
            shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            shrpr = context.getSharedPreferences("prefs_" + account, MODE_PRIVATE);
        }

        if (max_w < 250) {
            shrpr.edit().putBoolean(QuickstartPreferences.inet_only, true).apply();
        } else {
            shrpr.edit().putBoolean(QuickstartPreferences.inet_only, false).apply();
        }
        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        context.sendBroadcast(updateIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);


        if (intent.getAction().equalsIgnoreCase(ACTION_APPWIDGET_FORCE_UPDATE)) {
            int id = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                id = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            SharedPreferences sh = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
            String account = sh.getString(Integer.toString(id), "0");
            SharedPreferences shrpr = context.getSharedPreferences("prefs_" + account, MODE_PRIVATE);
            Boolean setting_update = shrpr.getBoolean(QuickstartPreferences.setting_update, true);
            if (setting_update.equals(true)) {
                //if in the settings update on tap is set
                UPD = "1";
            } else {
                UPD = "0";
            }
            shrpr.edit().putString(QuickstartPreferences.update, UPD).apply();
            shrpr.edit().putBoolean(QuickstartPreferences.f_update, true).apply();
            Intent updateIntent = new Intent(context, TraffWidget.class);
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { id });
            context.sendBroadcast(updateIntent);
        }
    }

    private String getDate(long timeStamp){

        try{
            DateFormat data = DateFormat.getDateInstance(DateFormat.SHORT);
            Locale currentLocale = Locale.getDefault();
            DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT, currentLocale);
            Date netDate = (new Date(timeStamp));
            return time.format(netDate) + "\n" + data.format(netDate);
        }
        catch(Exception ex){
            return "0";
        }
    }

    private String plurals(Long n, String form1, String form2, String form5){
        //day days days (день дня дней)
        if (n==0) return n.toString() + " " + form5;
        n = Math.abs(n) % 100;
        Long n1 = n % 10;
        if (n > 10 && n < 20) return n.toString() + " " + form5;
        if (n1 > 1 && n1 < 5) return n.toString() + " " + form2;
        if (n1 == 1) return n.toString() + " " + form1;
        return n.toString() + " " + form5;
    }


    public void setAllTextTuNull(RemoteViews widgetView) {
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

    public int getStringResourceByName(String aString, Context ctx) {
        String packageName = ctx.getPackageName();
        return ctx.getResources().getIdentifier(aString, "id", packageName);
    }


    public void setIntent(RemoteViews widgetView, Context context, int widgetID, String res) {
        //set intent to reload widget on tap

        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);

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

    public String getSize(String s) {
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
            //Log.d(LOG_TAG, "a true " + Integer.toString(Math.round(p/10)));
            return Integer.toString(Math.round(p/10)) + result;
        } else {
            //Log.d(LOG_TAG, "Received: " + Float.toString(p));
            p = Math.round(p);
            //Log.d(LOG_TAG, "round: " + Float.toString(p));
            p = p / 10;
            //Log.d(LOG_TAG, "division: " + Float.toString(p));
            return Float.toString(p) + result;
        }

    }

    public void setTextSize(RemoteViews widgetView, Context context, int widgetID, String res, AppWidgetManager appWidgetManager) {
        int min_h = appWidgetManager.getAppWidgetOptions(widgetID).getInt(OPTION_APPWIDGET_MAX_HEIGHT);
        float multiplier = (float) 5;
        Float size = min_h / multiplier;
        widgetView.setFloat(getStringResourceByName("inet" + res, context), "setTextSize", size);
        widgetView.setFloat(getStringResourceByName("calls" + res, context), "setTextSize", size);
        widgetView.setFloat(getStringResourceByName("sms" + res, context), "setTextSize", size);
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {
        //Get data && update widget


        //setting global vars
        contextglobal = context;
        appWidgetManagerglobal = appWidgetManager;

        SharedPreferences shrp = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences sh = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        String account = sh.getString(Integer.toString(widgetID), "0");
        String working_prefs = sh.getString("working_prefs", "prefs_0");
        SharedPreferences shrpr;
        if (working_prefs.equals("prefs_" + account)) {
            shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            shrpr = context.getSharedPreferences("prefs_" + account, MODE_PRIVATE);
        }
        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrp.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);
        Boolean inet_only = shrpr.getBoolean(QuickstartPreferences.inet_only, false);



        String max;
        String time;
        String ok;
        String leftmin;
        String leftsms;
        String dtn;
        String maxmin;
        String maxsms;
        String dtr;
        String balance;
        String date = "";
        String left;
        String null_;
        String res = "";

        time = shrpr.getString(QuickstartPreferences.time, "0");
        ok = shrpr.getString(QuickstartPreferences.ok, "");
        leftmin = shrpr.getString(QuickstartPreferences.leftmin, "");
        leftsms =  shrpr.getString(QuickstartPreferences.leftsms, "");
        maxmin = shrpr.getString(QuickstartPreferences.maxmin, "");
        maxsms =  shrpr.getString(QuickstartPreferences.maxsms, "");
        max = shrpr.getString(QuickstartPreferences.max, "");
        dtn = shrpr.getString(QuickstartPreferences.dtn, "0");
        dtr = shrpr.getString(QuickstartPreferences.dtr, "");
        left = shrpr.getString(QuickstartPreferences.left, "");
        balance = shrpr.getString(QuickstartPreferences.balance, "");
        null_ = shrpr.getString(QuickstartPreferences.null_, "");


        try {
            //convert unix epoch timestamp (seconds) to milliseconds
            long timestamp = Long.parseLong(time) * 1000L;
            date = getDate(timestamp);


            long days_to_new = Long.parseLong(dtn);
            //dtn = plurals(days_to_new, "день", "дня", "дней");
            dtn = plurals(days_to_new,
                    context.getString(R.string.day),
                    context.getString(R.string.days2),
                    context.getString(R.string.days));


            long days_to_restore = Long.parseLong(dtr);
            dtr = plurals(days_to_restore,
                    context.getString(R.string.day),
                    context.getString(R.string.days2),
                    context.getString(R.string.days));

        } catch (NumberFormatException e){
            Log.e(LOG_TAG, e.getMessage());
            //content = "error";
        }
        //}
        //Reformat login
        if (login.startsWith("+7")) {
            login = login.substring(2);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
        }



        if (op.equals("tele2")) {
            login = "7" + login;
        }
        pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        pass = shrpr.getString(QuickstartPreferences.pass, "");
        return_ = shrpr.getString(QuickstartPreferences.return_, "calc");

        UPD = shrpr.getString(QuickstartPreferences.update, "1");


        //Load timezone
        tz = TimeZone.getDefault().getID();

        //load app version
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);



        //load color
        int color =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);

        int text;
        //set text color
        String color_text = shrpr.getString(QuickstartPreferences.color_text, "null");
        if (color_text.equals("null")){
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            shrpr.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
        } else {
            try {
                text = Color.parseColor(color_text);
                color = text;
                shrpr.edit().putInt(QuickstartPreferences.color, color).apply();
            } catch (IllegalArgumentException e) {
                color = 0xffffffff;
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                shrpr.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
            }
        }
        //Colorize icons should be async
        //TODO
        new Colorize().StartColorize(context);

        //set text font
        String font =  shrpr.getString(QuickstartPreferences.font, "n");

        //update
        if (content.equals(context.getString(R.string.updating))) {
            Log.d(LOG_TAG, "update");

            Integer[] params = { widgetID };
            new ProgressTask().execute(params);
            if (null_.equals("false") && !inet_only) {
                Toast.makeText(context, context.getString(R.string.reduce_widget), Toast.LENGTH_LONG).show();
            }
        }

        setAllTextTuNull(widgetView);
        if (f_update) {
            //if widget is reloaded by tap
            shrpr.edit().putString(QuickstartPreferences.update, "1").apply();

            if (font.equals("n")) {
                widgetView.setTextViewText(R.id.text_upd, context.getString(R.string.updating));
                widgetView.setTextColor(R.id.text_upd, color);
            } else if (font.equals("i")) {
                widgetView.setTextViewText(R.id.text_upd_italic, context.getString(R.string.updating));
                widgetView.setTextColor(R.id.text_upd_italic, color);
            } else if (font.equals("b")) {
                widgetView.setTextViewText(R.id.text_upd_bold, context.getString(R.string.updating));
                widgetView.setTextColor(R.id.text_upd_bold, color);
            }

            shrpr.edit().putBoolean(QuickstartPreferences.f_update, false).apply();
        } else {

            if (ok.equals(""))  {
                if (!content.equals(context.getString(R.string.updating))) {
                    //if we have no data by server on first run
                    if (font.equals("n")) {
                        widgetView.setTextViewText(R.id.text_upd, context.getString(R.string.error));
                        widgetView.setTextColor(R.id.text_upd, color);
                    } else if (font.equals("i")) {
                        widgetView.setTextViewText(R.id.text_upd_italic, context.getString(R.string.error));
                        widgetView.setTextColor(R.id.text_upd_italic, color);
                    } else if (font.equals("b")) {
                        widgetView.setTextViewText(R.id.text_upd_bold, context.getString(R.string.error));
                        widgetView.setTextColor(R.id.text_upd_bold, color);
                    }
                } else {
                    //TODO: move to strings
                    if (font.equals("n")) {
                        widgetView.setTextViewText(R.id.text_upd, "Нажмите для выбора аккаунта");
                        widgetView.setTextColor(R.id.text_upd, color);
                    } else if (font.equals("i")) {
                        widgetView.setTextViewText(R.id.text_upd_italic, "Нажмите для выбора аккаунта");
                        widgetView.setTextColor(R.id.text_upd_italic, color);
                    } else if (font.equals("b")) {
                        widgetView.setTextViewText(R.id.text_upd_bold, "Нажмите для выбора аккаунта");
                        widgetView.setTextColor(R.id.text_upd_bold, color);
                    }
                }
            } else {
                String sms;
                String min;
                String inet;
                if (return_.equals("calc")) {
                    sms = maxsms;
                    min = maxmin;
                    inet = max;
                } else {
                    sms = leftsms;
                    min = leftmin;
                    inet = left;
                }
//                int string_re;
//                String days;
                Boolean minus = (Integer.parseInt(inet) < 0 || Integer.parseInt(min) < 0 || Integer.parseInt(sms) < 0);
//                if (minus) {
//                    //if we have something < 0 we need to return days to regain (provided by server)
//                    string_re = R.string.restore;
//                    days = dtr;
//                } else {
//                    string_re = R.string.renew;
//                    days = dtn;
//                }



                if (font.equals("n")) {
                    res = "";
                } else if (font.equals("i")){
                    res = "_italic";
                } else if (font.equals("b")) {
                    res = "_bold";
                }

                widgetView.setTextViewText(getStringResourceByName("inet" + res, context), getSize(inet));



                //get dada dir for get icons
                PackageManager m = context.getPackageManager();
                String s = context.getPackageName();
                try {
                    PackageInfo p = m.getPackageInfo(s, 0);
                    s = p.applicationInfo.dataDir;
                } catch (PackageManager.NameNotFoundException e) {

                }


                if (!null_.equals("false") && !inet_only) {
                    //if cals and sms are available

                    //for text in center of image
                    if (sms.length() == 1){
                        widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + "  ");
                    } else if (sms.length() == 2) {
                        widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + " ");
                    } else {
                        widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + "");
                    }
                    widgetView.setTextViewText(getStringResourceByName("calls" + res, context), min);



                    Bitmap b_icon_sms = BitmapFactory.decodeFile(s + "/files/sms" + account + ".png");
                    widgetView.setImageViewBitmap(R.id.sms_logo, b_icon_sms);

                    Bitmap b_icon_calls = BitmapFactory.decodeFile(s + "/files/calls" + account + ".png");
                    widgetView.setImageViewBitmap(R.id.calls_logo, b_icon_calls);

                    widgetView.setTextColor(getStringResourceByName("calls" + res, context), color);
                    widgetView.setTextColor(getStringResourceByName("sms" + res, context), color);
                }
                String inet_add = "";
                if (inet_only) {
                    //load another text view from layout in case if widget is small
                    inet_add = "_inet";
                }

                widgetView.setTextViewText(getStringResourceByName("balance" + res, context), " " + balance + " \u20BD");
                widgetView.setTextViewText(getStringResourceByName("date" + inet_add  + res, context), date);
                String nl = "\n";
                if (inet_only && appWidgetManager.getAppWidgetOptions(widgetID).getInt(OPTION_APPWIDGET_MIN_WIDTH) > 140) {
                    nl = " ";
                }
                if (!minus) {
                    //everything is more than zero
                    widgetView.setTextViewText(getStringResourceByName("renew" + inet_add + res, context), context.getString(R.string.renew_a) + nl + dtn);
                } else {
                    widgetView.setTextViewText(getStringResourceByName("renew" + inet_add + res, context), context.getString(R.string.restore) + " " + dtr + "\n" + context.getString(R.string.renew) + " " + dtn);
                }

                widgetView.setTextColor(getStringResourceByName("inet" + res, context), color);


                //setTextSize
                setTextSize(widgetView, context, widgetID, res, appWidgetManager);

                //widgetView.set
                widgetView.setTextColor(getStringResourceByName("balance" + res, context), color);
                widgetView.setTextColor(getStringResourceByName("date" + inet_add  + res, context), color);
                widgetView.setTextColor(getStringResourceByName("renew" + inet_add + res, context), color);




                //get icon from data folder
                Bitmap b_icon_inet = BitmapFactory.decodeFile(s  + "/files/inet" + account + ".png");
                widgetView.setImageViewBitmap(R.id.inet_logo, b_icon_inet);

            }


        }


        //make widget responsive to taps
        setIntent(widgetView, context, widgetID, res);

        //redraw widget
        appWidgetManager.updateAppWidget(widgetID, widgetView);

        return "";
    }






    private class ProgressTask extends AsyncTask<Integer, String, String> {
        //load new data from server
        @Override
        public String doInBackground(Integer... id) {


            try {
                //Loading content
                getContent(id[0]);
            } catch (IOException ex) {
                updateWidget(contextglobal, appWidgetManagerglobal, id[0], "error: doInBackground");
            }
            return Integer.toString(id[0]);
        }



        @Override
        protected void onPostExecute(String result) {
            int id = Integer.parseInt(result);
            //update widget after loading data
            updateWidget(contextglobal, appWidgetManagerglobal, id, "onPostExecute");
        }


        private String getContent(Integer id) throws IOException {
            BufferedReader reader;
            try {
                //connect to server
                URL url = new URL("https://srvr.tk/traf.php?cmd=json&upd=" + URLEncoder.encode(UPD, "UTF-8") +
                        "&login=" + URLEncoder.encode(login, "UTF-8") +
                        "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                        "&op=" + URLEncoder.encode(op, "UTF-8") +
                        "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                        "&pin=" + URLEncoder.encode(pin_code, "UTF-8") +
                        //"&loc=" + URLEncoder.encode(loc, "UTF-8") +
                        "&version=" + URLEncoder.encode(version, "UTF-8") +
                        "&token=" + URLEncoder.encode(token, "UTF-8") +
                        //"&return=" + URLEncoder.encode(return_, "UTF-8") +
                        "&tz=" + URLEncoder.encode(tz, "UTF-8")
                        //testing new logic for dtr
                        + "&test"
                );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line);
                }
                String buffer = buf.toString();
                try{
                    //parse answer
                    JSONObject jsonObject = new JSONObject(buffer);
                    String time = jsonObject.getString("time");
                    String ok = jsonObject.getString("ok");
                    String max = jsonObject.getString("max");
                    String maxmin = jsonObject.getString("maxmin");
                    String maxsms = jsonObject.getString("maxsms");
                    String leftmin = jsonObject.getString("leftmin");
                    String leftsms = jsonObject.getString("leftsms");
                    String dtr = jsonObject.getString("dtr");
                    String dtn = jsonObject.getString("dtn");
                    String left = jsonObject.getString("left");
                    String balance = jsonObject.getString("balance");
                    String null_ = jsonObject.getString("null");
//                    SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(contextglobal);

                    SharedPreferences sh = contextglobal.getSharedPreferences("MainPrefs", MODE_PRIVATE);
                    String account = sh.getString(Integer.toString(id), "0");
                    String working_prefs = sh.getString("working_prefs", "prefs_0");
                    SharedPreferences shrpr;
                    if (working_prefs.equals("prefs_" + account)) {
                        Log.d(LOG_TAG, "Using default prefs for save data");
                        shrpr = PreferenceManager.getDefaultSharedPreferences(contextglobal);
                    } else {
                        Log.d(LOG_TAG, "Using prefs_" + account + "for save data");
                        shrpr = contextglobal.getSharedPreferences("prefs_" + account, MODE_PRIVATE);
                    }
                    //save data
                    shrpr.edit().putString(QuickstartPreferences.time, time).apply();
                    shrpr.edit().putString(QuickstartPreferences.ok, ok).apply();
                    shrpr.edit().putString(QuickstartPreferences.maxmin, maxmin).apply();
                    shrpr.edit().putString(QuickstartPreferences.maxsms, maxsms).apply();
                    shrpr.edit().putString(QuickstartPreferences.leftmin, leftmin).apply();
                    shrpr.edit().putString(QuickstartPreferences.leftsms, leftsms).apply();
                    shrpr.edit().putString(QuickstartPreferences.max, max).apply();
                    shrpr.edit().putString(QuickstartPreferences.dtr, dtr).apply();
                    shrpr.edit().putString(QuickstartPreferences.dtn, dtn).apply();
                    shrpr.edit().putString(QuickstartPreferences.left, left).apply();
                    shrpr.edit().putString(QuickstartPreferences.balance, balance).apply();
                    shrpr.edit().putString(QuickstartPreferences.null_, null_).apply();

                   //update widget
                    updateWidget(contextglobal, appWidgetManagerglobal, id, "success");
                    return "Success";

                } catch (JSONException e){
                    //update widget with old data
                    updateWidget(contextglobal, appWidgetManagerglobal, id, "error: JSONException: " + e.getMessage());
                    return e.getMessage();
                }


            } catch (IOException e) {
                //update widget with old data
                updateWidget(contextglobal, appWidgetManagerglobal, id, "error: IOException: " + e.getMessage());
                return e.getMessage();
            }
        }


    }

}
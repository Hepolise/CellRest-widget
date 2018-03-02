package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TimeZone;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import android.widget.RemoteViews;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;


import ru.hepolise.cellrest.Activities.AccountChooser;
import ru.hepolise.cellrest.Utils.Colorize;
import ru.hepolise.cellrest.Utils.QuickstartPreferences;
import ru.hepolise.cellrest.Utils.WidgetUtils;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH;
import static android.content.Context.MODE_PRIVATE;
import static ru.hepolise.cellrest.Utils.WidgetUtils.getSharedPrefsByWidgetId;
import static ru.hepolise.cellrest.Utils.WidgetUtils.getSize;
import static ru.hepolise.cellrest.Utils.WidgetUtils.getStringResourceByName;
import static ru.hepolise.cellrest.Utils.WidgetUtils.plurals;
import static ru.hepolise.cellrest.Utils.WidgetUtils.setAllTextNull;
import static ru.hepolise.cellrest.Utils.WidgetUtils.setIntent;
import static ru.hepolise.cellrest.Utils.WidgetUtils.setTextSize;

public class TraffWidget extends AppWidgetProvider {
    static Context contextglobal;
    static android.appwidget.AppWidgetManager appWidgetManagerglobal;
    static String UPD;
    static String ACTION_APPWIDGET_FORCE_UPDATE = "";

    static String version;
    static String token;
    static String tz;
    static Boolean calc;

    final static String LOG_TAG = "cellLogsWidget";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int id : appWidgetIds) {
            Log.d(LOG_TAG, "Starting update: " + id);
            updateWidget(context, appWidgetManager, id, context.getString(R.string.updating));

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i(LOG_TAG, "deleted: " + appWidgetIds[0]);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        sharedPreferences.edit().remove("widget_id_" + Integer.toString(appWidgetIds[0])).commit();
    }
    @Override
    public void onAppWidgetOptionsChanged (Context context,
                                           AppWidgetManager appWidgetManager,
                                           int appWidgetId,
                                           Bundle newOptions) {
        int max_w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);

        SharedPreferences shrpr = getSharedPrefsByWidgetId(context, appWidgetId);

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
            Log.d(LOG_TAG, "onReceive: " + id);
            SharedPreferences shrpr = getSharedPrefsByWidgetId(context, id);
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



    static public void updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {
        // Get data && update widget

        String login;
        String op;

        //setting global vars
        contextglobal = context;
        appWidgetManagerglobal = appWidgetManager;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        long ts = sharedPreferences.getLong("widget_id_"+Integer.toString(widgetID), 0);
        Log.d(LOG_TAG, "widget id: " + widgetID);

        SharedPreferences shrpr = getSharedPrefsByWidgetId(context, widgetID);


        Intent updateIntent;
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        android.app.PendingIntent pIntent;
        if (ts == 0 || "def".equals(shrpr.getString("thisPrefs", "def"))) {
            content = context.getString(R.string.choose_account);

            Log.d(LOG_TAG, "ts is null or shared prefs are not initialized");
            //updateIntent = new Intent(context, AccountChooser.class);
            updateIntent = Intent.makeRestartActivityTask(new ComponentName(context, AccountChooser.class));
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            updateIntent.putExtra("id", widgetID);
            updateIntent.putExtra("from", "TraffWidget");
            pIntent = PendingIntent.getActivity(context, widgetID, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            updateIntent = new Intent(context, TraffWidget.class);
            updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
            pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        }

        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);
        Boolean inet_only = shrpr.getBoolean(QuickstartPreferences.inet_only, false);
        calc = shrpr.getBoolean(QuickstartPreferences.calc, true);


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
            // convert unix epoch timestamp (seconds) to milliseconds
            long timestamp = Long.parseLong(time) * 1000L;
            date = WidgetUtils.getDate(timestamp);


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
        }
        // Reformat login
        if (login.startsWith("+7")) {
            login = login.substring(2);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
        }
        if (op.equals("tele2")) {
            login = "7" + login;
        }
        Log.d(LOG_TAG, "login: "+login);

        UPD = shrpr.getString(QuickstartPreferences.update, "1");

        //Load timezone
        tz = TimeZone.getDefault().getID();

        //load app version
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);

        // load color
        int color =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);

        int text;
        // set text color
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
        new Colorize().StartColorize(context);

        // set text font
        String font =  shrpr.getString(QuickstartPreferences.font, "n");

        // update
        if (content.equals(context.getString(R.string.updating))) {
            Log.d(LOG_TAG, "update");

            Integer[] params = { widgetID };
            Log.d(LOG_TAG, "Starting progress task: " + widgetID);
            new DownloadData().execute(params);
            if (null_.equals("false") && !inet_only) {
                Toast.makeText(context, context.getString(R.string.reduce_widget), Toast.LENGTH_LONG).show();
            }
        }

        setAllTextNull(widgetView);
        if (f_update && ! content.equals(context.getString(R.string.choose_account))) {
            Log.d(LOG_TAG, "force update");
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

            if (ok.equals("") || content.equals(context.getString(R.string.choose_account)))  {
                Log.d(LOG_TAG, "ok is empty or ts is null or prefs are not init");
                if (!content.equals(context.getString(R.string.updating)) && !content.equals(context.getString(R.string.choose_account))) {
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
                    Log.d(LOG_TAG, "But content is update");
                    if (content.equals(context.getString(R.string.choose_account))) {
                        Log.d(LOG_TAG, "content is to choose account");
                        if (font.equals("n")) {
                            widgetView.setTextViewText(R.id.text_upd, context.getString(R.string.choose_account));
                            widgetView.setTextColor(R.id.text_upd, color);
                        } else if (font.equals("i")) {
                            widgetView.setTextViewText(R.id.text_upd_italic, context.getString(R.string.choose_account));
                            widgetView.setTextColor(R.id.text_upd_italic, color);
                        } else if (font.equals("b")) {
                            widgetView.setTextViewText(R.id.text_upd_bold, context.getString(R.string.choose_account));
                            widgetView.setTextColor(R.id.text_upd_bold, color);
                        }
                    } else {
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
                    }
                }
            } else {
                    Log.d(LOG_TAG, "Everything is OK, setting full widget view");
                    String sms;
                    String min;
                    String inet;
                    if (calc) {
                        sms = maxsms;
                        min = maxmin;
                        inet = max;
                    } else {
                        sms = leftsms;
                        min = leftmin;
                        inet = left;
                    }
                    Boolean minus = (Integer.parseInt(inet) < 0 ||
                            Integer.parseInt(min) < 0 ||
                            Integer.parseInt(sms) < 0);


                    if (font.equals("n")) {
                        res = "";
                    } else if (font.equals("i")) {
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
                        Log.e(LOG_TAG, e.getLocalizedMessage());
                    }


                    if (!null_.equals("false") && !inet_only) {
                        //if cals and sms are available

                        //for text in center of image
                        if (sms.length() == 1) {
                            widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + "  ");
                        } else if (sms.length() == 2) {
                            widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + " ");
                        } else {
                            widgetView.setTextViewText(getStringResourceByName("sms" + res, context), sms + "");
                        }
                        widgetView.setTextViewText(getStringResourceByName("calls" + res, context), min);


                        Bitmap b_icon_sms = BitmapFactory.decodeFile(s + "/files/sms_" + ts + ".png");
                        widgetView.setImageViewBitmap(R.id.sms_logo, b_icon_sms);

                        Bitmap b_icon_calls = BitmapFactory.decodeFile(s + "/files/calls_" + ts + ".png");
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
                    widgetView.setTextViewText(getStringResourceByName("date" + inet_add + res, context), date);
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
                    widgetView.setTextColor(getStringResourceByName("date" + inet_add + res, context), color);
                    widgetView.setTextColor(getStringResourceByName("renew" + inet_add + res, context), color);

                    //get icon from data folder
                    Bitmap b_icon_inet = BitmapFactory.decodeFile(s + "/files/inet_" + ts + ".png");
                    widgetView.setImageViewBitmap(R.id.inet_logo, b_icon_inet);
            }


        }
        //make widget responsive to taps
        setIntent(widgetView, context, res, pIntent);
        //redraw widget
        appWidgetManager.updateAppWidget(widgetID, widgetView);

    }






    static private class DownloadData extends AsyncTask<Integer, String, String> {
        String login;
        String pass;
        String op;
        String android_id;
        String pin_code;
        // load new data from server
        @Override
        public String doInBackground(Integer... id) {
            try {
                // Loading content
                loadVars(id[0]);
                getContent(id[0]);
            } catch (IOException ex) {
                updateWidget(contextglobal, appWidgetManagerglobal, id[0], "error: doInBackground");
            }
            return Integer.toString(id[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            int id = Integer.parseInt(result);
            // update widget after loading data
            updateWidget(contextglobal, appWidgetManagerglobal, id, "onPostExecute");
        }

        private void loadVars(int id) {
            SharedPreferences shrpr = getSharedPrefsByWidgetId(contextglobal, id);
            login = shrpr.getString(QuickstartPreferences.login, "");
            op = shrpr.getString(QuickstartPreferences.op_list, "");
            pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
            android_id = shrpr.getString(QuickstartPreferences.androidId, Settings.Secure.getString(contextglobal.getContentResolver(), Settings.Secure.ANDROID_ID));
            pass = shrpr.getString(QuickstartPreferences.pass, "");
            // Reformat login
            if (login.startsWith("+7")) {
                login = login.substring(2);
            } else if (login.startsWith("7") || login.startsWith("8")){
                login = login.substring(1);
            }
            if (op.equals("tele2")) {
                login = "7" + login;
            }
        }

        private void getContent(Integer id) throws IOException {
            BufferedReader reader;
            try {
                // connect to server
                Log.d(LOG_TAG, "getContent: "+id + " Login: " + login);
                URL url = new URL("https://srvr.su/traf.php?cmd=json&upd=" + URLEncoder.encode(UPD, "UTF-8") +
                        "&login=" + URLEncoder.encode(login, "UTF-8") +
                        "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                        "&op=" + URLEncoder.encode(op, "UTF-8") +
                        "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                        "&pin=" + URLEncoder.encode(pin_code, "UTF-8") +
                        "&version=" + URLEncoder.encode(version, "UTF-8") +
                        "&token=" + URLEncoder.encode(token, "UTF-8") +
                        "&tz=" + URLEncoder.encode(tz, "UTF-8")
                        // if some server tests
                        //+ "&test"
                );
                Log.d(LOG_TAG, url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line);
                }
                String buffer = buf.toString();
                JSONObject jsonObject;
                try{
                    // parse answer
                    jsonObject = new JSONObject(buffer);
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

                    SharedPreferences shrpr = getSharedPrefsByWidgetId(contextglobal, id);
                    // save data
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

                   // update widget
                    updateWidget(contextglobal, appWidgetManagerglobal, id, "success");

                } catch (JSONException e){
                    try {
                        jsonObject = new JSONObject(buffer);
                        String err = jsonObject.getString("error");
                        if (err.contains("Auth needed") || err.contains("Необходимо пройти регистрацию")) {
                            SharedPreferences shrpr = getSharedPrefsByWidgetId(contextglobal, id);
                            //shrpr.edit().remove(QuickstartPreferences.pin_code).commit();
                            shrpr.edit().putBoolean(QuickstartPreferences.tele2AuthDisabled, true).commit();
                        }
                    } catch (JSONException ej) {
                        Log.e(LOG_TAG, "Catch JSONException while catching JSONException" + ej.getLocalizedMessage());
                    }
                    // update widget with old data
                    Log.e(LOG_TAG, "JSON Exception: " + e.getLocalizedMessage());
                    updateWidget(contextglobal, appWidgetManagerglobal, id, "error: JSONException: " + e.getMessage());
                }


            } catch (IOException e) {
                // update widget with old data
                updateWidget(contextglobal, appWidgetManagerglobal, id, "error: IOException: " + e.getMessage());
            }
        }


    }

}
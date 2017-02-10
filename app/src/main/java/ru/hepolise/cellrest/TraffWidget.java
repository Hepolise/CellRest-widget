package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import android.widget.RemoteViews;


import org.json.JSONException;
import org.json.JSONObject;


import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;



public class TraffWidget extends AppWidgetProvider {
    Context conextglobal;
    android.appwidget.AppWidgetManager appWidgetManagerglobal;
    String UPD;
    String ACTION_APPWIDGET_FORCE_UPDATE = "";
    String login;
    String pass;
    String op;
    String android_id;
    String pin_code;
    String locale;
    String loc;
    String version;
    String token;
    String return_;
    String tz;


    final String LOG_TAG = "cellLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //Log.d(LOG_TAG, "onEnabled: ");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);


        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id, context.getString(R.string.updating));

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean setting_update = sharedPreferences.getBoolean(QuickstartPreferences.setting_update, true);
            if (setting_update.equals(true)) {
                //if in the settings update on tap is set
                UPD = "1";
            } else {
                UPD = "0";
            }
            SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(context);
            shpr.edit().putString(QuickstartPreferences.update, UPD).apply();
            shpr.edit().putBoolean(QuickstartPreferences.f_update, true).apply();
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
            DateFormat time = DateFormat.getTimeInstance(DateFormat.SHORT);
            Date netDate = (new Date(timeStamp));
            return time.format(netDate) + "\n" + data.format(netDate);
        }
        catch(Exception ex){
            return "0";
        }
    }

    private String plurals(Long n, String form1, String form2, String form5){
        if (n==0) return n.toString() + " " + form5;
        n = Math.abs(n) % 100;
        Long n1 = n % 10;
        if (n > 10 && n < 20) return n.toString() + " " + form5;
        if (n1 > 1 && n1 < 5) return n.toString() + " " + form2;
        if (n1 == 1) return n.toString() + " " + form1;
        return n.toString() + " " + form5;
    }
    public Bitmap colorize(Bitmap srcBmp, int dstColor) {

        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        float srcHSV[] = new float[3];
        float dstHSV[] = new float[3];

        Bitmap dstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = srcBmp.getPixel(col, row);
                int alpha = Color.alpha(pixel);
                Color.colorToHSV(pixel, srcHSV);
                Color.colorToHSV(dstColor, dstHSV);


                dstBitmap.setPixel(col, row, Color.HSVToColor(alpha, dstHSV));
            }
        }

        return dstBitmap;
    }

    public void setAllTextTuNull(RemoteViews widgetView) {
        //SET ALL TEXT TO NULL
        widgetView.setTextViewText(R.id.inet, "");
        widgetView.setTextViewText(R.id.calls, "");
        widgetView.setTextViewText(R.id.sms, "");
        widgetView.setTextViewText(R.id.balance, "");
        widgetView.setTextViewText(R.id.date, "");
        widgetView.setTextViewText(R.id.renew, "");

        widgetView.setTextViewText(R.id.inet_italic, "");
        widgetView.setTextViewText(R.id.calls_italic, "");
        widgetView.setTextViewText(R.id.sms_italic, "");
        widgetView.setTextViewText(R.id.balance_italic, "");
        widgetView.setTextViewText(R.id.date_italic, "");
        widgetView.setTextViewText(R.id.renew_italic, "");

        widgetView.setTextViewText(R.id.inet_bold, "");
        widgetView.setTextViewText(R.id.calls_bold, "");
        widgetView.setTextViewText(R.id.sms_bold, "");
        widgetView.setTextViewText(R.id.balance_bold, "");
        widgetView.setTextViewText(R.id.date_bold, "");
        widgetView.setTextViewText(R.id.renew_bold, "");


        //set update to null
        widgetView.setTextViewText(R.id.text_upd, "");
        widgetView.setTextViewText(R.id.text_upd_italic, "");
        widgetView.setTextViewText(R.id.text_upd_bold, "");

        //END SET ALL TEXT TO NULL
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {
        Log.d(LOG_TAG, "content: " + content);

        conextglobal = context;
        appWidgetManagerglobal = appWidgetManager;

        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);


        String max = "";
        String time = "";
        String ok = "";
        String leftmin = "";
        String leftsms = "";
        String dtn = "";
        String maxmin = "";
        String maxsms = "";
        String dtr = "";
        String balance = "";
        String date = "";
        String left = "";

        //if (content.equals("success")) {
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
        //balance = "10000";


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
        } catch (NumberFormatException e){
            Log.e(LOG_TAG, e.getMessage());
            //content = "error";
        }
        //}
        //Reformat login
        if (login.startsWith("+7")) {
            login = login.substring(2);
            //Log.d(LOG_TAG, "+7 change: " + login);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
            //Log.d(LOG_TAG, "7/8 change: " + login);
        }



        if (op.equals("tele2")) {
            login = "7" + login;
        }
        pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        pass = shrpr.getString(QuickstartPreferences.pass, "");
        return_ = shrpr.getString(QuickstartPreferences.return_, "calc");







        UPD = shrpr.getString(QuickstartPreferences.update, "1");


//        //Load location variable
//        loc = shrpr.getString(QuickstartPreferences.loc, "def");
//        Locale currentLocale = Locale.getDefault();
//        locale = currentLocale.toString();
//        if (loc.equals("def")) {
//            loc = locale;
//        }

        //Load timezone
        tz = TimeZone.getDefault().getID();
        //Log.d(LOG_TAG, tz);

        //loading app version
//        try {
//            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//
//        }
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);



        int color =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);

        int text;
        String color_text = shrpr.getString(QuickstartPreferences.color_text, "null");
        if (color_text.equals("null")){
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            shrpr.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
        } else {
            try {
                text = Color.parseColor(color_text);
                color = text;
                shrpr.edit().putInt(QuickstartPreferences.color, text).apply();
            } catch (IllegalArgumentException e) {
                color = 0;
            }
        }
        //Log.d(LOG_TAG, Integer.toString(text));






        String font =  shrpr.getString(QuickstartPreferences.font, "n");
        if (content.equals(context.getString(R.string.updating))) {
            //Log.d(LOG_TAG, "update");
            if (f_update) {
                //Log.d(LOG_TAG, "f-update");
                shrpr.edit().putString(QuickstartPreferences.update, "1").apply();



                setAllTextTuNull(widgetView);
                widgetView.setImageViewBitmap(R.id.calls_logo, null);
                widgetView.setImageViewBitmap(R.id.sms_logo, null);
                widgetView.setImageViewBitmap(R.id.inet_logo, null);
                //}
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
            }
            Integer[] params = { widgetID };
            new ProgressTask().execute(params);
        } else {
            //Log.d(LOG_TAG, "just set widget, check data");




            setAllTextTuNull(widgetView);
            if (ok.equals("")) {
                widgetView.setImageViewBitmap(R.id.calls_logo, null);
                widgetView.setImageViewBitmap(R.id.sms_logo, null);
                widgetView.setImageViewBitmap(R.id.inet_logo, null);
                //}
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
                //set text view

                //Log.d(LOG_TAG, "data is ok");
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


                if (font.equals("n")) {
                    //Log.d(LOG_TAG, "font: normal");
                    if (inet.length() < 3) {
                        widgetView.setTextViewText(R.id.inet, " " + inet + "  ");
                    } else {
                        widgetView.setTextViewText(R.id.inet, inet);
                    }

                    widgetView.setTextViewText(R.id.calls, min);
                    //for center of image
                    if (maxsms.length() % 2 == 0) {
                        widgetView.setTextViewText(R.id.sms, sms + " ");
                    } else {
                        widgetView.setTextViewText(R.id.sms, sms + "  ");
                    }
                    widgetView.setTextViewText(R.id.balance, " " + balance + " \u20BD");
                    widgetView.setTextViewText(R.id.date, date);
                    widgetView.setTextViewText(R.id.renew, context.getString(R.string.renew) + "\n" + dtn);


                    widgetView.setTextColor(R.id.inet, color);
                    widgetView.setTextColor(R.id.calls, color);
                    widgetView.setTextColor(R.id.sms, color);
                    widgetView.setTextColor(R.id.balance, color);
                    widgetView.setTextColor(R.id.date, color);
                    widgetView.setTextColor(R.id.renew, color);
                } else if (font.equals("i")) {
                    if (inet.length() < 3) {
                        widgetView.setTextViewText(R.id.inet_italic, " " + inet + "  ");
                    } else {
                        widgetView.setTextViewText(R.id.inet_italic, inet);
                    }
                    widgetView.setTextViewText(R.id.calls_italic, min);
                    //for center of image
                    if (maxsms.length() % 2 == 0) {
                        widgetView.setTextViewText(R.id.sms_italic, sms + " ");
                    } else {
                        widgetView.setTextViewText(R.id.sms_italic, sms + "  ");
                    }
                    widgetView.setTextViewText(R.id.balance_italic, balance + " \u20BD");
                    widgetView.setTextViewText(R.id.date_italic, date);
                    widgetView.setTextViewText(R.id.renew_italic, context.getString(R.string.renew) + "\n" + dtn);

                    widgetView.setTextColor(R.id.inet_italic, color);
                    widgetView.setTextColor(R.id.calls_italic, color);
                    widgetView.setTextColor(R.id.sms_italic, color);
                    widgetView.setTextColor(R.id.balance_italic, color);
                    widgetView.setTextColor(R.id.date_italic, color);
                    widgetView.setTextColor(R.id.renew_italic, color);
                } else if (font.equals("b")) {
                    if (inet.length() < 3) {
                        widgetView.setTextViewText(R.id.inet_bold, " " + inet + "  ");
                    } else {
                        widgetView.setTextViewText(R.id.inet_bold, inet);
                    }
                    widgetView.setTextViewText(R.id.calls_bold, min);
                    //for center of image
                    //if (maxsms.length() % 2 == 0) {
                    widgetView.setTextViewText(R.id.sms_bold, sms + " ");
                    //} else {
                    //    widgetView.setTextViewText(R.id.sms_bold, sms + "  ");
                    //
                    widgetView.setTextViewText(R.id.balance_bold, balance + " \u20BD");
                    widgetView.setTextViewText(R.id.date_bold, date);
                    widgetView.setTextViewText(R.id.renew_bold, context.getString(R.string.renew) + "\n" + dtn);


                    widgetView.setTextColor(R.id.inet_bold, color);
                    widgetView.setTextColor(R.id.calls_bold, color);
                    widgetView.setTextColor(R.id.sms_bold, color);
                    widgetView.setTextColor(R.id.balance_bold, color);
                    widgetView.setTextColor(R.id.date_bold, color);
                    widgetView.setTextColor(R.id.renew_bold, color);
                }


                //set image view
//                widgetView.setImageViewResource(R.id.calls_logo, 0);
//                widgetView.setImageViewResource(R.id.sms_logo, R.drawable.ic_message_white_48dp);
//                widgetView.setImageViewResource(R.id.inet_logo, R.drawable.ic_data_usage_white_48dp);


                //Log.d( LOG_TAG, String.valueOf(maxsms.length()));


                //Log.d(LOG_TAG, "setting colors and images");

                widgetView.setTextColor(R.id.inet, color);
                widgetView.setTextColor(R.id.calls, color);
                widgetView.setTextColor(R.id.sms, color);
                widgetView.setTextColor(R.id.balance, color);
                widgetView.setTextColor(R.id.date, color);
                widgetView.setTextColor(R.id.renew, color);


                Drawable icon_calls = ContextCompat.getDrawable(context, R.drawable.ic_local_phone_white_48dp);
                Bitmap b_icon_calls = ((BitmapDrawable) icon_calls).getBitmap();
                b_icon_calls = colorize(b_icon_calls, color);
                widgetView.setImageViewBitmap(R.id.calls_logo, b_icon_calls);

                //Drawable icon_inet = ContextCompat.getDrawable(context, R.drawable.ic_data_usage_white_48dp);
                Drawable icon_inet = ContextCompat.getDrawable(context, R.drawable.ic_language_white_48dp);
                Bitmap b_icon_inet = ((BitmapDrawable) icon_inet).getBitmap();
                b_icon_inet = colorize(b_icon_inet, color);
                widgetView.setImageViewBitmap(R.id.inet_logo, b_icon_inet);

                Drawable icon_sms = ContextCompat.getDrawable(context, R.drawable.ic_message_white_48dp);
                Bitmap b_icon_sms = ((BitmapDrawable) icon_sms).getBitmap();
                b_icon_sms = colorize(b_icon_sms, color);
                widgetView.setImageViewBitmap(R.id.sms_logo, b_icon_sms);


            }


            //Log.d(LOG_TAG, "done1");

        }

        //Log.d(LOG_TAG, "done2");


        // set Widget view


        //Working with colors



        //Set all text to null
//        widgetView.setTextViewText(R.id.inet, "");
//        widgetView.setTextViewText(R.id.calls, "");
//        widgetView.setTextViewText(R.id.sms, "");
//        widgetView.setTextViewText(R.id.balance, "");
//        if (Build.VERSION.SDK_INT >= 23) {
//            // Marshmallow+
//            widgetView.setImageViewIcon(R.id.calls_logo, null);
//            widgetView.setImageViewIcon(R.id.sms_logo, null);
//            widgetView.setImageViewIcon(R.id.inet_logo, null);
//        }

////        widgetView.setTextViewText(R.id.text_bold, "");
////        widgetView.setTextViewText(R.id.text_italic, "");
        //Changing text type
//        String font =  shrpr.getString(QuickstartPreferences.font, "n");
//        if (font.equals("i")) {
//            res = R.id.text_italic;
//        } else if(font.equals("b")) {
//            res = R.id.text_bold;
//        }


        //Toast.makeText(context, max, Toast.LENGTH_SHORT).show();
        //Setting content to widget and updating it


        //Log.d(LOG_TAG, "setting update intent");
        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);

        widgetView.setOnClickPendingIntent(R.id.text_upd, pIntent);
        widgetView.setOnClickPendingIntent(R.id.inet, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms, pIntent);
        widgetView.setOnClickPendingIntent(R.id.inet_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.balance, pIntent);

        //widgetView.setOnClickPendingIntent(R.id.text_upd_italic, pIntent);
        widgetView.setOnClickPendingIntent(R.id.inet_italic, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls_italic, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms_italic, pIntent);
        widgetView.setOnClickPendingIntent(R.id.balance_italic, pIntent);
        widgetView.setOnClickPendingIntent(R.id.text_upd_italic, pIntent);

        //widgetView.setOnClickPendingIntent(R.id.text_upd_bold, pIntent);
        widgetView.setOnClickPendingIntent(R.id.inet_bold, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls_bold, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms_bold, pIntent);
        widgetView.setOnClickPendingIntent(R.id.balance_bold, pIntent);
        widgetView.setOnClickPendingIntent(R.id.text_upd_bold, pIntent);
        //for make all-widgets app in future
        //TODO
        //Log.d(LOG_TAG, "rebuilding widget: " + Integer.toString(widgetID));
        ComponentName name = new ComponentName(context, TraffWidget.class);
        int [] ids = appWidgetManager.getAppWidgetIds(name);
        for (int i: ids) {
            Log.d(LOG_TAG, Integer.toString(i));
        }
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        //Log.d(LOG_TAG, "widget is rebuilt...");
        return "";
    }

    //new ProgressTask().execute();
    class ProgressTask extends AsyncTask<Integer, String, String> {
        @Override
        public String doInBackground(Integer... id) {


            try {
                //Loading content
                getContent(id[0]);
            } catch (IOException ex) {
                updateWidget(conextglobal, appWidgetManagerglobal, id[0], "error: doInBackground");
            }
            return Integer.toString(id[0]);
        }



        @Override
        protected void onPostExecute(String result) {
            Log.d(LOG_TAG, "onpostexec result: "  + result);
            int id = Integer.parseInt(result);
            Log.d(LOG_TAG, "onpostexec id "  + id);
            updateWidget(conextglobal, appWidgetManagerglobal, id, "onPostExecute");
        }


        private String getContent(Integer id) throws IOException {
            BufferedReader reader;

            try {

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
                        //+ "&test"
                );
                //Log.d(LOG_TAG, "URL: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line);
                }
                String buffer = buf.toString();
                //Log.d(LOG_TAG, buffer);
                try{
                    JSONObject jsonObject = new JSONObject(buffer);
//                    String error = jsonObject.getString("error");
//                    //Log.d("json", "error " + error);
                    String time = jsonObject.getString("time");
                    //Log.d("json", "time " + time);
                    String ok = jsonObject.getString("ok");
                    //Log.d("json", "ok " + ok);
                    String foo = jsonObject.getString("foo");
                    //Log.d("json", "foo " + foo);
                    String bar = jsonObject.getString("bar");
                    //Log.d("json", "bar " + bar);
                    String max = jsonObject.getString("max");
                    //Log.d("json", "max " + max);
                    String maxmin = jsonObject.getString("maxmin");
                    //Log.d("json", "maxmin " + maxmin);
                    String maxsms = jsonObject.getString("maxsms");
                    //Log.d("json", "maxsms " + maxsms);
                    String leftmin = jsonObject.getString("leftmin");
                    //Log.d("json", "leftmin " + leftmin);
                    String leftsms = jsonObject.getString("leftsms");
                    //Log.d("json", "leftsms " + leftsms);
                    String dtr = jsonObject.getString("dtr");
                    //Log.d("json", "dtr " + dtr);
                    String dtn = jsonObject.getString("dtn");
                    //Log.d("json", "dtn " + dtn);
                    String left = jsonObject.getString("left");
                    //Log.d("json", "left " + left);
                    String balance = jsonObject.getString("balance");
                    //Log.d("json", "balance " + balance);
                    //Log.d("json", jsonObject.toString());
                    SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(conextglobal);
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

                    updateWidget(conextglobal, appWidgetManagerglobal, id, "success");
                    return "Success";

                } catch (JSONException e){
//                    JSONObject jsonObject = new JSONObject(buffer)
//                    String error = jsonObject.getString("error");
//                    //Log.d("json", "error " + error);
                    //Log.d("json", e.getMessage());
                    updateWidget(conextglobal, appWidgetManagerglobal, id, "error :JSONException" + e.getMessage());
                    return e.getMessage();
                }


            } catch (IOException e) {
                //Log.d(LOG_TAG, e.getMessage());
                updateWidget(conextglobal, appWidgetManagerglobal, id, "error: IOException" + e.getMessage());
                return e.getMessage();
            }




        }


    }

}
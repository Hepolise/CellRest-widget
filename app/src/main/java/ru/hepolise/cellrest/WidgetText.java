package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH;


public class WidgetText extends AppWidgetProvider {
    Context contextglobal;
    android.appwidget.AppWidgetManager appWidgetManagerglobal;
    int idglobal;
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
    Float size;


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
            contextglobal = context;
            appWidgetManagerglobal = appWidgetManager;
            idglobal = id;
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
//        int max_h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT);
//        int max_w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);
//        int min_h = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT);
//        int min_w = newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH);
//        Log.d(LOG_TAG, "max_h: " + max_h);
//        Log.d(LOG_TAG, "max_w: " + max_w);
//        Log.d(LOG_TAG, "min_h: " + min_h);
//        Log.d(LOG_TAG, "min_w: " + min_w);
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
                UPD = "1";
            } else {
                UPD = "0";
            }
            SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(context);
            shpr.edit().putString(QuickstartPreferences.update, UPD).apply();
            shpr.edit().putBoolean(QuickstartPreferences.f_update, true).apply();
            Intent updateIntent = new Intent(context, WidgetText.class);
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { id });
            context.sendBroadcast(updateIntent);
        }
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {
        //Log.d(LOG_TAG, "upd 11");


        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);

//        String language_code = "en_US";
//
//        Resources res1 = context.getResources();
//        // Change locale settings in the app.
//        DisplayMetrics dm = res1.getDisplayMetrics();
//        android.content.res.Configuration conf = res1.getConfiguration();
//        conf.locale = new Locale(language_code.toLowerCase());
//        res1.updateConfiguration(conf, dm);
//
//        String languageToLoad  = "en"; // your language
//        Locale locale1 = new Locale(languageToLoad);
//        Locale.setDefault(locale1);
//        Configuration config = new Configuration();
//        config.locale = locale1;
//        context.getResources().updateConfiguration(config,
//                context.getResources().getDisplayMetrics());

        //in case of error loading new data
        if (content.startsWith("error")) {
            content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
        }

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
        

        //Load location variable
        loc = shrpr.getString(QuickstartPreferences.loc, "def");
        Locale currentLocale = Locale.getDefault();
        locale = currentLocale.toString();
        if (loc.equals("def")) {
            loc = locale;
        }
        Log.d(LOG_TAG, loc);

        //Load timezone
        tz = TimeZone.getDefault().getID();
        //Log.d(LOG_TAG, tz);

        //loading app version
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);

        if (content.equals(context.getString(R.string.updating))) {
            if (f_update) {
                shrpr.edit().putBoolean(QuickstartPreferences.f_update, false).apply();
            } else {
                content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
            }

            //Starting to load content
            Integer[] params = { widgetID };
            new ProgressTask().execute(params);
            shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
        } else {
            //Save new content
            shrpr.edit().putString(QuickstartPreferences.content, content).apply();
        }


        //shrpr.getStringSet()


        Set<String> pattern =  shrpr.getStringSet(QuickstartPreferences.pattern, null);
        String p = "";
        try {
            p = pattern.toString();
            Log.d(LOG_TAG, "Success run");
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "First run");
            p = "check_days check_bal check_bal";
        }

        Log.d(LOG_TAG, p);


        int f = content.indexOf("\n");
        int s = content.indexOf("\n", f+1);
        int t = content.indexOf("\n", s+1);
        int a = content.length();
        //Log.d(LOG_TAG, Integer.toString(f) + " " + Integer.toString(s) + " " + Integer.toString(t) + " " + Integer.toString(a));
        //int lines = 3;
        String newContent = "";
        if ( ! content.equals(context.getString(R.string.error)) && ! content.equals(context.getString(R.string.updating)) ) {
            if (p.contains("check_days")) {
                newContent = content.substring(0, f) + "\n";
            }
            newContent = newContent +  content.substring(f+1, s);
            if (p.contains("check_bal")) {
                newContent = newContent + content.substring(s, t);
            }
            if (p.contains("check_ts")) {
                newContent = newContent + content.substring(t, a);
            }

            content = newContent;
        }

//        Log.d(LOG_TAG, "newc: " + newContent);
//        Log.d(LOG_TAG, "c: " + content);



        // set Widget view

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget_text);


        //set text size
        size = (float) shrpr.getInt(QuickstartPreferences.text_size, 16);
        widgetView.setFloat(R.id.text_light, "setTextSize", size);
        widgetView.setFloat(R.id.text_bold, "setTextSize", size);
        widgetView.setFloat(R.id.text_italic, "setTextSize",size);

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

        //Set all text to null
        widgetView.setTextViewText(R.id.text_light, "");
        widgetView.setTextViewText(R.id.text_bold, "");
        widgetView.setTextViewText(R.id.text_italic, "");
        int res = R.id.text_light;
        //Changing text type
        String font =  shrpr.getString(QuickstartPreferences.font, "n");
        if (font.equals("i")) {
            res = R.id.text_italic;
        } else if(font.equals("b")) {
            res = R.id.text_bold;
        }


        //Setting content to widget and updating it
        widgetView.setTextViewText(res, content);
        widgetView.setTextColor(res, color);
        Intent updateIntent = new Intent(context, WidgetText.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(res, pIntent);
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        return (null);
    }

    class ProgressTask extends AsyncTask<Integer, String, String> {
        @Override
        public String doInBackground(Integer... id) {

            try {
                //Loading content
                getContent(id[0]);
            } catch (IOException ex) {
                updateWidget(contextglobal, appWidgetManagerglobal, id[0], "error");
            }
            return Integer.toString(id[0]);
        }

//        @Override
//        protected void onPostExecute(String result) {
//            int id = Integer.parseInt(result);
//            //update widget after loading data
//            updateWidget(contextglobal, appWidgetManagerglobal, id, "onPostExecute");
//        }

        private String getContent(Integer id) throws IOException {
            BufferedReader reader;

            try {

                URL url = new URL("https://srvr.tk/traf.php?cmd=widget&upd=" + URLEncoder.encode(UPD, "UTF-8") +
                        "&login=" + URLEncoder.encode(login, "UTF-8") +
                        "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                        "&op=" + URLEncoder.encode(op, "UTF-8") +
                        "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                        "&pin=" + URLEncoder.encode(pin_code, "UTF-8") +
                        "&loc=" + URLEncoder.encode(loc, "UTF-8") +
                        "&version=" + URLEncoder.encode(version, "UTF-8") +
                        "&token=" + URLEncoder.encode(token, "UTF-8") +
                        "&return=" + URLEncoder.encode(return_, "UTF-8") +
                        "&tz=" + URLEncoder.encode(tz, "UTF-8")
                        //+ "&test"
                );
                //Log.d(LOG_TAG, "URL: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + " ");
                }
                String buffer = buf.toString();
                if (buffer.contains("NEWLINE")) {
                    buffer = buffer.replace(" NEWLINE ", "\n");
                } else {
                    buffer = "error";
                }
                updateWidget(contextglobal, appWidgetManagerglobal, id, buffer);
                return(buffer);

            } catch (IOException e) {
                //Log.d(LOG_TAG, e.getMessage());
                updateWidget(contextglobal, appWidgetManagerglobal, id, "error");
                return e.getMessage();
            }

        }

    }
}

package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import ru.hepolise.cellrest.Activities.AccountChooser;
import ru.hepolise.cellrest.Utils.QuickstartPreferences;
import ru.hepolise.cellrest.Utils.WidgetUtils;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.content.Context.MODE_PRIVATE;


public class WidgetText extends AppWidgetProvider {
    static Context contextglobal;
    static android.appwidget.AppWidgetManager appWidgetManagerglobal;
    int idglobal;
    static String UPD;
    static String ACTION_APPWIDGET_FORCE_UPDATE = "";

    static String locale;
    static String loc;
    static String version;
    static String token;
    static String tz;
    static Float size;


    final static String LOG_TAG = "cellLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
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
        Log.i(LOG_TAG, "deleted: " + appWidgetIds[0]);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        sharedPreferences.edit().remove("widget_id_" + Integer.toString(appWidgetIds[0])).commit();
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
            SharedPreferences sharedPreferences = WidgetUtils.getSharedPrefsByWidgetId(context, id);
            Boolean setting_update = sharedPreferences.getBoolean(QuickstartPreferences.setting_update, true);
            if (setting_update.equals(true)) {
                UPD = "1";
            } else {
                UPD = "0";
            }
            sharedPreferences.edit().putString(QuickstartPreferences.update, UPD).apply();
            sharedPreferences.edit().putBoolean(QuickstartPreferences.f_update, true).apply();
            Intent updateIntent = new Intent(context, WidgetText.class);
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { id });
            context.sendBroadcast(updateIntent);
        }
    }

    static public void updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {
        context.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        long ts = sharedPreferences.getLong("widget_id_"+Integer.toString(widgetID), 0);

        SharedPreferences shrpr = WidgetUtils.getSharedPrefsByWidgetId(context, widgetID);


        Intent updateIntent;
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget_text);
        android.app.PendingIntent pIntent;
        if (ts == 0 || "def".equals(shrpr.getString("thisPrefs", "def"))) {
            content = context.getString(R.string.choose_account);
            updateIntent = Intent.makeRestartActivityTask(new ComponentName(context, AccountChooser.class));
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            updateIntent.putExtra("id", widgetID);
            updateIntent.putExtra("from", "WidgetText");
            pIntent = PendingIntent.getActivity(context, widgetID, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            updateIntent = new Intent(context, WidgetText.class);
            updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
            pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        }

        // Load vars
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);

        // in case of error loading new data
        if (content.startsWith("error")) {
            content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
        }

        UPD = shrpr.getString(QuickstartPreferences.update, "1");
        // Load location variable
        loc = shrpr.getString(QuickstartPreferences.loc, "def");
        Locale currentLocale = Locale.getDefault();
        locale = currentLocale.toString();
        if (loc.equals("def")) {
            loc = locale;
        }
        //Log.d(LOG_TAG, loc);

        // Load timezone
        tz = TimeZone.getDefault().getID();
        //Log.d(LOG_TAG, tz);

        // loading app version
        int versionCode = BuildConfig.VERSION_CODE;
        version = Integer.toString(versionCode);

        if (content.equals(context.getString(R.string.updating))) {
            //Log.d(LOG_TAG, "The content is for update");
            if (f_update) {
                //Log.d(LOG_TAG, "force update");
                shrpr.edit().putBoolean(QuickstartPreferences.f_update, false).apply();
            } else {
                content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.updating));
            }

            // Starting to load content
            Integer[] params = { widgetID };
            new DownloadData().execute(params);
            shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
        } else {
            if (!content.equals(context.getString(R.string.choose_account))) {
                // Save new content
                shrpr.edit().putString(QuickstartPreferences.content, content).apply();
            }
        }

        Set<String> pattern =  shrpr.getStringSet(QuickstartPreferences.pattern, null);
        String p = "";
        try {
            p = pattern.toString();
            //Log.d(LOG_TAG, "Success run");
        } catch (NullPointerException e) {
            //Log.d(LOG_TAG, "First run");
            p = "check_days check_data check_bal check_ts";
        }

        //Log.d(LOG_TAG, p);


        int f = content.indexOf("\n");
        int s = content.indexOf("\n", f+1);
        int t = content.indexOf("\n", s+1);
        int a = content.length();
        //Log.d(LOG_TAG, Integer.toString(f) + " " + Integer.toString(s) + " " + Integer.toString(t) + " " + Integer.toString(a));
        try {
            String newContent = "";
            if (!content.equals(context.getString(R.string.error))
                    && !content.equals(context.getString(R.string.updating))
                    && !content.equals(context.getString(R.string.choose_account))) {
                Boolean nl = false;
                if (p.contains("check_days")) {
                    newContent = content.substring(0, f);
                    nl = true;
                }
                if (p.contains("check_data")) {
                    if (!nl) f = f + 1;
                    newContent = newContent + content.substring(f, s);
                    nl = true;
                }
                if (p.contains("check_bal")) {
                    if (!nl) s = s + 1;
                    newContent = newContent + content.substring(s, t);
                    nl = true;
                }
                if (p.contains("check_ts")) {
                    if (!nl) t = t + 1;
                    newContent = newContent + content.substring(t, a);
                }

                content = newContent;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception (text): " + e.getLocalizedMessage());
        }

//        //Log.d(LOG_TAG, "newc: " + newContent);
//        //Log.d(LOG_TAG, "c: " + content);




        // set text size
        size = (float) shrpr.getInt(QuickstartPreferences.text_size, 16);
        widgetView.setFloat(R.id.text_light, "setTextSize", size);
        widgetView.setFloat(R.id.text_bold, "setTextSize", size);
        widgetView.setFloat(R.id.text_italic, "setTextSize",size);

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

        // Set all text to null
        widgetView.setTextViewText(R.id.text_light, "");
        widgetView.setTextViewText(R.id.text_bold, "");
        widgetView.setTextViewText(R.id.text_italic, "");
        int res = R.id.text_light;
        // Changing text type
        String font =  shrpr.getString(QuickstartPreferences.font, "n");
        if (font.equals("i")) {
            res = R.id.text_italic;
        } else if(font.equals("b")) {
            res = R.id.text_bold;
        }


        // Setting content to widget and updating it
        widgetView.setTextViewText(res, content);
        widgetView.setTextColor(res, color);
        //Log.d(LOG_TAG, "setting pending intent");
        widgetView.setOnClickPendingIntent(res, pIntent);
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    static class DownloadData extends AsyncTask<Integer, String, String> {
        String login;
        String pass;
        String op;
        String android_id;
        String pin_code;
        String return_;
        @Override
        public String doInBackground(Integer... id) {

            try {
                // Loading content
                loadVars(id[0]);
                getContent(id[0]);
            } catch (IOException ex) {
                updateWidget(contextglobal, appWidgetManagerglobal, id[0], "error");
            }
            return Integer.toString(id[0]);
        }

        private void loadVars(int id) {
            SharedPreferences shrpr = WidgetUtils.getSharedPrefsByWidgetId(contextglobal, id);
            login = shrpr.getString(QuickstartPreferences.login, "");
            op = shrpr.getString(QuickstartPreferences.op_list, "");
            pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
            android_id = shrpr.getString(QuickstartPreferences.androidId, Settings.Secure.getString(contextglobal.getContentResolver(), Settings.Secure.ANDROID_ID));
            pass = shrpr.getString(QuickstartPreferences.pass, "");
            Boolean calc = shrpr.getBoolean(QuickstartPreferences.calc, true);
            if (calc) {
                return_ = "calc";
            } else {
                return_ = "full";
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
        }
        private void getContent(Integer id) throws IOException {
            BufferedReader reader;
            try {
                URL url = new URL("https://srvr.su/traf.php?cmd=widget&upd=" + URLEncoder.encode(UPD, "UTF-8") +
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
                        // some server tests
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
                Log.d(LOG_TAG,"buffer: " + buffer);
                if (buffer.contains("NEWLINE")) {
                    buffer = buffer.replace(" NEWLINE ", "\n");
                } else if (buffer.contains("Error: Auth needed") || buffer.contains("Error: Необходимо пройти регистрацию")) {
                    SharedPreferences shrpr = WidgetUtils.getSharedPrefsByWidgetId(contextglobal, id);
                    //shrpr.edit().remove(QuickstartPreferences.pin_code).commit();
                    shrpr.edit().putBoolean(QuickstartPreferences.tele2AuthDisabled, true).commit();
                } else {
                    buffer = "error";
                }
                updateWidget(contextglobal, appWidgetManagerglobal, id, buffer);
                //return(buffer);

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                updateWidget(contextglobal, appWidgetManagerglobal, id, "error");
                //return e.getMessage();
            }

        }

    }
}

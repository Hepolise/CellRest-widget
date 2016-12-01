package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH;


public class TraffWidget extends AppWidgetProvider {
    Context conextglobal;
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
            conextglobal = context;
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
        int max_h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT);
        int max_w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);
        int min_h = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT);
        int min_w = newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH);
        Log.d(LOG_TAG, "max_h: " + max_h);
        Log.d(LOG_TAG, "max_w: " + max_w);
        Log.d(LOG_TAG, "min_h: " + min_h);
        Log.d(LOG_TAG, "min_w: " + min_w);
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
            Intent updateIntent = new Intent(context, TraffWidget.class);
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { id });
            context.sendBroadcast(updateIntent);
        }
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {


        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean admin;

        //in case of error loading new data
        if (content.startsWith("error")) {
            content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
        }

        //Reformat login
        if (login.startsWith("+7")) {
            login = login.substring(2);
            Log.d(LOG_TAG, "+7 change: " + login);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
            Log.d(LOG_TAG, "7/8 change: " + login);
        }


        admin = login.equals("");

        if (op.equals("tele2") && !admin) {
            login = "7" + login;
        }
        pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        pass = shrpr.getString(QuickstartPreferences.pass, "");
        return_ = shrpr.getString(QuickstartPreferences.return_, "calc");





        if (admin) {
            //Do not update first run if admin account
            UPD = shrpr.getString(QuickstartPreferences.update, "0");
        } else {
            UPD = shrpr.getString(QuickstartPreferences.update, "1");
        }

        //Load location variable
        loc = shrpr.getString(QuickstartPreferences.loc, "def");
        Locale currentLocale = Locale.getDefault();
        locale = currentLocale.toString();
        if (loc.equals("def")) {
            loc = locale;
        }

        //loading app version
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        int versionCode = BuildConfig.VERSION_CODE;

        if (content.equals(context.getString(R.string.updating))) {
            //content = context.getString(R.string.updating);
            //Starting to load content
            new ProgressTask().execute();
            if (admin) {
                // in case of admin's account
                if (UPD.equals("1")) {
                    shrpr.edit().putString(QuickstartPreferences.update, "0").apply();
                }
            } else {
                shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
            }
        } else {
            //Save new content
            shrpr.edit().putString(QuickstartPreferences.content, content).apply();
        }


        // set Widget view

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        //Working with colors
        int color =  shrpr.getInt(QuickstartPreferences.color, 0xff4d4d4d);
        Boolean default_color =  shrpr.getBoolean(QuickstartPreferences.default_color, true);
        if (default_color.equals(true)) {
            color = 0xff4d4d4d;
            shrpr.edit().putInt(QuickstartPreferences.color, -11711155).apply();
        }


        //Setting warning about admin's account
        if (admin && (!content.equals(context.getString(R.string.updating)))){
            widgetView.setTextViewText(R.id.text_default, context.getString(R.string.admin_acc));
            widgetView.setTextColor(R.id.text_default, color);
        } else {
            widgetView.setTextViewText(R.id.text_default, "");
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
        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(res, pIntent);
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        return (null);
    }

    class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... path) {

            try {
                //Loading content
                getContent();
            } catch (IOException ex) {
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "error");
            }
            return null;
        }


        private String getContent() throws IOException {
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
                        "&return=" + URLEncoder.encode(return_, "UTF-8")
                        //+ "&test"
                        );
                Log.d(LOG_TAG, "URL: " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + " ");
                }
                String buffer = buf.toString();
                buffer = buffer.replace(" NEWLINE ", "\n");
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, buffer);
                return(buffer);

            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage());
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "error");
                return e.getMessage();
            }

        }

    }
}
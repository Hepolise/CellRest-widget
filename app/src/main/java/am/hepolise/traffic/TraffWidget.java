package am.hepolise.traffic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.R.attr.action;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static java.security.AccessController.getContext;


public class TraffWidget extends AppWidgetProvider {
    //String content;
    Context conextglobal;
    android.appwidget.AppWidgetManager appWidgetManagerglobal;
    int idglobal;
    String UPD;
    String ACTION_APPWIDGET_FORCE_UPDATE = "";
    String login;
    String pass;
    String op;
    String android_id;
    //String smscode;
    String pin_code;
    String locale;
    //String ACTION_MINICALLWIDGET_CLICKED;




    final String LOG_TAG = "traffLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //Log.d(LOG_TAG, "onEnabled: ");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));

        for (int id : appWidgetIds) {
            conextglobal = context;
            appWidgetManagerglobal = appWidgetManager;
            idglobal = id;

            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //sharedPreferences.edit().putInt(QuickstartPreferences.WId, id).apply();



            //new ProgressTask().execute();
            //content = "Updating...";
            //UPD = "0";
            updateWidget(context, appWidgetManager, id, "Updating...");

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //Log.d(LOG_TAG, "onDisabled");
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        //Log.d(LOG_TAG, "onReceive");

        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        // find your TextView here by id here and update it.

        //if (WIDGET_BUTTON.equals(intent.getAction())) {
        //Toast.makeText(context, "Clicked!!", Toast.LENGTH_SHORT).show();

        //}


        if (intent.getAction().equalsIgnoreCase(ACTION_APPWIDGET_FORCE_UPDATE)) {
            //String action = intent.getAction();
            //int id = 0;
            int id = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                id = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            //id = Integer.parseInt(action.substring(ACTION_APPWIDGET_FORCE_UPDATE.length()));
            //Log.d(LOG_TAG, "id: " + id);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean setting_update = sharedPreferences.getBoolean(QuickstartPreferences.setting_update, true);
            if (setting_update.equals(true)) {
                UPD = "1";
            } else {
                UPD = "0";
            }
            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //int id = sharedPreferences.getInt(QuickstartPreferences.WId, 0);

            SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(context);
            shpr.edit().putString(QuickstartPreferences.update, UPD).apply();

            //updateWidget(context, appWidgetManagerr, id);
            //Toast.makeText(context, "UPD: " + UPD, Toast.LENGTH_SHORT).show();
            Intent updateIntent = new Intent(context, TraffWidget.class);
            updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    new int[] { id });
            context.sendBroadcast(updateIntent);
        } else {
            //Toast.makeText(context, "Updating widget: " + UPD, Toast.LENGTH_SHORT).show();
        }

        //SharedPreferences SharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPrefs.edit().putString(QuickstartPreferences.update, UPD).apply();
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {


        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        UPD = shrpr.getString(QuickstartPreferences.update, "1");

        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        //smscode = shrpr.getString(QuickstartPreferences.smscode, "");


        if (content.equals("error")) {
            //Log.d(LOG_TAG, "not accessible");
            //Toast.makeText(context, "Server is unreachable", Toast.LENGTH_SHORT).show();
            content = shrpr.getString(QuickstartPreferences.content, "");
        }
        //Log.d(LOG_TAG, content);

        if (login.startsWith("+7")) {
            login = login.substring(2);
            Log.d(LOG_TAG, "+7 change: " + login);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
            Log.d(LOG_TAG, "7/8 change: " + login);
        }

        if (op.equals("tele2")) {
            login = "7" + login;
            //Log.d(LOG_TAG, "tele2 change: " + login);
            pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
            pass = "null";
            android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(LOG_TAG, "android_id " + android_id);
        } else {
            pass = shrpr.getString(QuickstartPreferences.pass, "");
        }

        Locale currentLocale = Locale.getDefault();
        locale = currentLocale.toString();

        if (content.equals("Updating...")) {
            //Log.d(LOG_TAG, "exec");
            new ProgressTask().execute();
            if (login.equals("") || pass.equals("")) {
                //Log.d(LOG_TAG, "null");
                if (UPD.equals("1")) {
                    //Log.d(LOG_TAG, "updateWidget (change UPD)");
                    shrpr.edit().putString(QuickstartPreferences.update, "0").apply();
                }
            } else {
                //Log.d(LOG_TAG, "not null " + pass + " " + login);
                shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
            }
            //Toast.makeText(context, "Update: " + UPD, Toast.LENGTH_SHORT).show();
        } else {
            shrpr.edit().putString(QuickstartPreferences.content, content).apply();
        }

        int color =  shrpr.getInt(QuickstartPreferences.color, 0xff4d4d4d);
        Boolean default_color =  shrpr.getBoolean(QuickstartPreferences.default_color, true);
        if (default_color.equals(true)) {
            color = 0xff4d4d4d;
            shrpr.edit().putInt(QuickstartPreferences.color, -11711155).apply();
        }
        String font =  shrpr.getString(QuickstartPreferences.font, "n");
        // Настраиваем внешний вид виджета
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        widgetView.setTextViewText(R.id.text_light, "");
        widgetView.setTextViewText(R.id.text_bold, "");
        widgetView.setTextViewText(R.id.text_italic, "");
        int res = R.id.text_light;
        if (font.equals("i")) {
            //Log.d(LOG_TAG, "i " + font);
            res = R.id.text_italic;
        } else if(font.equals("b")) {
            //Log.d(LOG_TAG, "b " + font);
            res = R.id.text_bold;
        }


        widgetView.setTextViewText(res, content);
        widgetView.setTextColor(res, color);

        //widgetView.setTextColor(R.id.tv, );
        //SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //shared_prefs.edit().putString(QuickstartPreferences.content, content).apply();




        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(res, pIntent);

        // Обновляем виджет
        //appWidgetManager.up
        appWidgetManager.updateAppWidget(widgetID, widgetView);
        //}
        return (null);
    }

    class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... path) {

            try {

                getContent();
                //Log.d(LOG_TAG, "getContent: " + cont);
            } catch (IOException ex) {
                //Log.d(LOG_TAG, "Error: " + ex.getMessage());
            }
            return null;
        }


        private String getContent() throws IOException {
            BufferedReader reader;

            try {
                URL url = new URL("https://srvr.tk/traf.php?cmd=widget&upd=" + UPD + "&login=" + login + "&pass=" + pass + "&op=" + op + "&devid=" + android_id + "&pin=" + pin_code + "&loc=" + locale);
                //Log.d(LOG_TAG, "url: " + url);
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
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "error");
                return e.getMessage();
            }

        }

    }
}
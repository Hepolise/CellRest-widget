package am.hepolise.traffic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.R.attr.action;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;


public class TraffWidget extends AppWidgetProvider {
    //String content;
    Context conextglobal;
    android.appwidget.AppWidgetManager appWidgetManagerglobal;
    int idglobal;
    String UPD;
    String ACTION_APPWIDGET_FORCE_UPDATE = "";
    String login;
    String pass;
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
            Toast.makeText(context, "Updating widget: " + UPD, Toast.LENGTH_SHORT).show();
        }

        //SharedPreferences SharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPrefs.edit().putString(QuickstartPreferences.update, UPD).apply();
    }

    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {


        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        UPD = shrpr.getString(QuickstartPreferences.update, "1");
        pass = shrpr.getString(QuickstartPreferences.pass, "");
        login = shrpr.getString(QuickstartPreferences.login, "");

        if (content.equals("Updating...")) {
            //Log.d(LOG_TAG, "exec");
            new ProgressTask().execute();
            if (login.equals("") || pass.equals("")) {
                Log.d(LOG_TAG, "null");
                if (UPD.equals("1")) {
                    //Log.d(LOG_TAG, "updateWidget (change UPD)");
                    SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(context);
                    shpr.edit().putString(QuickstartPreferences.update, "0").apply();
                }
            } else {
                Log.d(LOG_TAG, "not null " + pass + " " + login);
                SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(context);
                shpr.edit().putString(QuickstartPreferences.update, "1").apply();
            }
            //Toast.makeText(context, "Update: " + UPD, Toast.LENGTH_SHORT).show();
        } //else {

        // Настраиваем внешний вид виджета
        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        widgetView.setTextViewText(R.id.tv, content);
        //widgetView.u

        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.tv, pIntent);

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

                String cont = getContent();
                //Log.d(LOG_TAG, "getContent: " + cont);
            } catch (IOException ex) {
                //Log.d(LOG_TAG, "Error: " + ex.getMessage());
            }
            return null;
        }


        private String getContent() throws IOException {
            BufferedReader reader;

            try {
                URL url = new URL("https://srvr.tk/traf.php?cmd=widget&upd=" + UPD + "&login=" + login + "&pass=" + pass + "&op=bee");
                Log.d(LOG_TAG, "url: " + url);
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
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, e.getMessage());
                return e.getMessage();
            }

        }

    }
}
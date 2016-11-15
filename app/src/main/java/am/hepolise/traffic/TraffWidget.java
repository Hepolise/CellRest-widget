package am.hepolise.traffic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        Boolean admin;

        //in case of error loading new data
        if (content.equals("error")) {
            content = shrpr.getString(QuickstartPreferences.content, "");
        }

        //Reformat login
        if (login.startsWith("+7")) {
            login = login.substring(2);
            Log.d(LOG_TAG, "+7 change: " + login);
        } else if (login.startsWith("7") || login.startsWith("8")){
            login = login.substring(1);
            Log.d(LOG_TAG, "7/8 change: " + login);
        }

        //Edit vars for cellular operators
        if (op.equals("tele2")) {
            login = "7" + login;
            pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
            pass = "null";
            android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(LOG_TAG, "android_id " + android_id);
        } else {
            pass = shrpr.getString(QuickstartPreferences.pass, "");
        }

        if (login.equals("") || pass.equals("")) {
            admin = true;
        } else {
            admin = false;
        }

        if (admin) {
            //Do not update first run if admin acconut
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

        if (content.equals(context.getString(R.string.updating))) {
            content = context.getString(R.string.updating);
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
            }
            return null;
        }


        private String getContent() throws IOException {
            BufferedReader reader;

            try {
                URL url = new URL("https://srvr.tk/traf.php?cmd=widget&upd=" + UPD + "&login=" + login + "&pass=" + pass + "&op=" + op + "&devid=" + android_id + "&pin=" + pin_code + "&loc=" + loc + "&version=" + version);
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
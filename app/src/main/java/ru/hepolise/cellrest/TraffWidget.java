package ru.hepolise.cellrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.*;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.name;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH;
import static ru.hepolise.cellrest.R.id.container;


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
//
//    @Override
//    public void onAppWidgetOptionsChanged (Context context,
//                                    AppWidgetManager appWidgetManager,
//                                    int appWidgetId,
//                                    Bundle newOptions) {
//        int max_h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT);
//        int max_w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);
//        int min_h = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT);
//        int min_w = newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH);
//        Log.d(LOG_TAG, "max_h: " + max_h);
//        Log.d(LOG_TAG, "max_w: " + max_w);
//        Log.d(LOG_TAG, "min_h: " + min_h);
//        Log.d(LOG_TAG, "min_w: " + min_w);
//    }

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
            DateFormat sdf = DateFormat.getTimeInstance();
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
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

                // If it area to be painted set only value of original image
                //dstHSV[2] = srcHSV[2];  // value
                dstBitmap.setPixel(col, row, Color.HSVToColor(alpha, dstHSV));
            }
        }

        return dstBitmap;
    }

//    public Bitmap getColoredBitmap(int color, Bitmap srcBmp) {
//        //Bitmap source = BitmapFactory.decodeResource(context.getResources(),
//                //drawableId);
//        final Bitmap bitmap = Bitmap.createBitmap(srcBmp.getWidth(),
//                srcBmp.getHeight(), Bitmap.Config.ARGB_8888);
//        for (int i = 0; i < srcBmp.getWidth(); i++) {
//            for (int j = 0; j < srcBmp.getHeight(); j++) {
//                int pixel = srcBmp.getPixel(i, j);
//
//                // if (pixel == Color.TRANSPARENT) {
//                //
//                // } else
//                if (pixel == Color.WHITE) {
//                    pixel = Color.argb(Color.alpha(pixel),
//                            Color.red(Color.WHITE), Color.green(Color.WHITE),
//                            Color.blue(Color.WHITE));
//                } else {
//                    pixel = Color.argb(Color.alpha(pixel), Color.red(color),
//                            Color.green(color), Color.blue(color));
//                }
//                bitmap.setPixel(i, j, pixel);
//            }
//        }
//        return  bitmap;
//    }



    public String updateWidget(Context context, AppWidgetManager appWidgetManager,
                               int widgetID, String content) {


        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(context);
        //Load vars
        login = shrpr.getString(QuickstartPreferences.login, "");
        op = shrpr.getString(QuickstartPreferences.op_list, "");
        token = shrpr.getString(QuickstartPreferences.TOKEN, "");
        Boolean f_update = shrpr.getBoolean(QuickstartPreferences.f_update, false);

        //in case of error loading new data
//        if (content.startsWith("error")) {
//            content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
//        }


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

        //if (content.equals("success")) {
            time = shrpr.getString(QuickstartPreferences.time, "");
            ok = shrpr.getString(QuickstartPreferences.ok, "");
            leftmin = shrpr.getString(QuickstartPreferences.leftmin, "");
            leftsms =  shrpr.getString(QuickstartPreferences.leftsms, "");
            maxmin = shrpr.getString(QuickstartPreferences.maxmin, "");
            maxsms =  shrpr.getString(QuickstartPreferences.maxsms, "");
            max = shrpr.getString(QuickstartPreferences.max, "");
            dtn = shrpr.getString(QuickstartPreferences.dtn, "");
            dtr = shrpr.getString(QuickstartPreferences.dtr, "");
            balance = shrpr.getString(QuickstartPreferences.balance, "");


            //convert unix epoch timestamp (seconds) to milliseconds
            long timestamp = Long.parseLong(time) * 1000L;
            date = getDate(timestamp );


            long days_to_new = Long.parseLong(dtn);
            dtn = plurals(days_to_new, "день", "дня", "дней");
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
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        //int versionCode = BuildConfig.VERSION_CODE;

        RemoteViews widgetView = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        int color =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);
        //color = 0x00000000;


        if (content.equals(context.getString(R.string.updating))) {
            if (f_update) {






                shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
                widgetView.setTextViewText(R.id.inet, "");
                widgetView.setTextViewText(R.id.calls, "");
                widgetView.setTextViewText(R.id.sms, "");
                widgetView.setTextViewText(R.id.balance, "");
                widgetView.setTextViewText(R.id.date, "");
                widgetView.setTextViewText(R.id.renew, "");
                //if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                widgetView.setImageViewResource(R.id.calls_logo, 0);
                widgetView.setImageViewResource(R.id.sms_logo, 0);
                widgetView.setImageViewResource(R.id.inet_logo, 0);
                //}
                widgetView.setTextViewText(R.id.text_upd, context.getString(R.string.updating));
                widgetView.setTextColor(R.id.text_upd, color);






                shrpr.edit().putBoolean(QuickstartPreferences.f_update, false).apply();
            } else {
                content = shrpr.getString(QuickstartPreferences.content, context.getString(R.string.error));
            }
            //content = context.getString(R.string.updating);
            //Starting to load content
            new ProgressTask().execute();
//            if (admin) {
//                // in case of admin's account
//                if (UPD.equals("1")) {
//                    shrpr.edit().putString(QuickstartPreferences.update, "0").apply();
//                }
//            } else {
//                shrpr.edit().putString(QuickstartPreferences.update, "1").apply();
//            }

        } else {
            //Save new content
            shrpr.edit().putString(QuickstartPreferences.content, content).apply();
            widgetView.setTextViewText(R.id.inet, max);
            widgetView.setTextViewText(R.id.calls, maxmin);


            //for center of image
            if(maxsms.length() % 2 == 0) {
                widgetView.setTextViewText(R.id.sms, maxsms + " ");
            } else {
                widgetView.setTextViewText(R.id.sms, maxsms + "  ");
            }


            widgetView.setTextViewText(R.id.balance, balance + " \u20BD");
            widgetView.setTextViewText(R.id.date, date);
            widgetView.setTextViewText(R.id.renew, "Сброс через\n" + dtn);
            //if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
            widgetView.setImageViewResource(R.id.calls_logo, 0);
            widgetView.setImageViewResource(R.id.sms_logo, R.drawable.ic_message_white_48dp);
            widgetView.setImageViewResource(R.id.inet_logo, R.drawable.ic_data_usage_white_48dp);
            //}


            //Log.d( LOG_TAG, String.valueOf(maxsms.length()));




            widgetView.setTextViewText(R.id.text_upd, "");

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

            Drawable icon_inet = ContextCompat.getDrawable(context, R.drawable.ic_data_usage_white_48dp);
            Bitmap b_icon_inet = ((BitmapDrawable) icon_inet).getBitmap();
            b_icon_inet = colorize(b_icon_inet, color);
            widgetView.setImageViewBitmap(R.id.inet_logo, b_icon_inet);

            Drawable icon_sms = ContextCompat.getDrawable(context, R.drawable.ic_message_white_48dp);
            Bitmap b_icon_sms = ((BitmapDrawable) icon_sms).getBitmap();
            b_icon_sms = colorize(b_icon_sms, color);
            widgetView.setImageViewBitmap(R.id.sms_logo, b_icon_sms);





        }



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



        Intent updateIntent = new Intent(context, TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_FORCE_UPDATE);

        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        android.app.PendingIntent pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.inet, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms, pIntent);
        widgetView.setOnClickPendingIntent(R.id.inet_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.calls_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.sms_logo, pIntent);
        widgetView.setOnClickPendingIntent(R.id.balance, pIntent);
        widgetView.setOnClickPendingIntent(R.id.text_upd, pIntent);
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

                URL url = new URL("https://srvr.tk/traf.php?cmd=json&upd=" + URLEncoder.encode(UPD, "UTF-8") +
                        "&login=" + URLEncoder.encode(login, "UTF-8") +
                        "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                        "&op=" + URLEncoder.encode(op, "UTF-8") +
                        "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                        "&pin=" + URLEncoder.encode(pin_code, "UTF-8") +
                        //"&loc=" + URLEncoder.encode(loc, "UTF-8") +
                        "&version=" + URLEncoder.encode(version, "UTF-8") +
                        "&token=" + URLEncoder.encode(token, "UTF-8") +
                        "&return=" + URLEncoder.encode(return_, "UTF-8") +
                        "&tz=" + URLEncoder.encode(tz, "UTF-8")
                        + "&test"
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
                Log.d(LOG_TAG, buffer);
                try{
                    JSONObject jsonObject = new JSONObject(buffer);
//                    String error = jsonObject.getString("error");
//                    Log.d("json", "error " + error);
                    String time = jsonObject.getString("time");
                    Log.d("json", "time " + time);
                    String ok = jsonObject.getString("ok");
                    Log.d("json", "ok " + ok);
                    String foo = jsonObject.getString("foo");
                    Log.d("json", "foo " + foo);
                    String bar = jsonObject.getString("bar");
                    Log.d("json", "bar " + bar);
                    String max = jsonObject.getString("max");
                    Log.d("json", "max " + max);
                    String maxmin = jsonObject.getString("maxmin");
                    Log.d("json", "maxmin " + maxmin);
                    String maxsms = jsonObject.getString("maxsms");
                    Log.d("json", "maxsms " + maxsms);
                    String leftmin = jsonObject.getString("leftmin");
                    Log.d("json", "leftmin " + leftmin);
                    String leftsms = jsonObject.getString("leftsms");
                    Log.d("json", "leftsms " + leftsms);
                    String dtr = jsonObject.getString("dtr");
                    Log.d("json", "dtr " + dtr);
                    String dtn = jsonObject.getString("dtn");
                    Log.d("json", "dtn " + dtn);
                    String balance = jsonObject.getString("balance");
                    Log.d("json", "balance " + balance);
                    Log.d("json", jsonObject.toString());
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
                    shrpr.edit().putString(QuickstartPreferences.balance, balance).apply();

                    updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "success");
                    return(buffer);

                } catch (JSONException e){
                    Log.d("json", e.getMessage());
                    updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "error");
                    return e.getMessage();
                }


            } catch (IOException e) {
                //Log.d(LOG_TAG, e.getMessage());
                updateWidget(conextglobal, appWidgetManagerglobal, idglobal, "error");
                return e.getMessage();
            }

        }

    }
}
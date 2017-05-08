package ru.hepolise.cellrest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hepolise on 25.03.17.
 */


public class Colorize {
    Context contextglobal;
    String LOG_TAG = "cellLogs";



    public String StartColorize(Context ctx) {

        contextglobal = ctx;
        new ColorizeBitmap().execute();
        return "";
    }
    private Bitmap colorize(Bitmap srcBmp, int dstColor) {
        //change images color

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
    private class ColorizeBitmap extends AsyncTask<Integer, String, String> {


        @Override
        public  String doInBackground(Integer... id) {

            FileOutputStream out = null;
            try {

                SharedPreferences sh;
                SharedPreferences sharedPreferences = contextglobal.getSharedPreferences("MainPrefs", MODE_PRIVATE);
                int accounts = sharedPreferences.getInt("account", 0);

                for (int i=0; i<=accounts; i++) {
                    sh = contextglobal.getSharedPreferences("prefs_" + Integer.toString(i), MODE_PRIVATE);


                    int color =  sh.getInt(QuickstartPreferences.color, 0xffffffff);


                    //set text color
                    String color_text = sh.getString(QuickstartPreferences.color_text, "null");
                    if (color_text.equals("null")){
                        String hexColor = String.format("#%06X", (0xFFFFFF & color));
                        sh.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
                    } else {
                        try {
                            color = Color.parseColor(color_text);
                            sh.edit().putInt(QuickstartPreferences.color, color).apply();
                        } catch (IllegalArgumentException e) {
                            color = 0xffffffff;
                            String hexColor = String.format("#%06X", (0xFFFFFF & color));
                            sh.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
                        }
                    }

                    //inet
                    Drawable icon_inet = ContextCompat.getDrawable(contextglobal, R.drawable.ic_language_white_48dp);
                    Bitmap b_icon_inet = ((BitmapDrawable) icon_inet).getBitmap();
                    b_icon_inet = colorize(b_icon_inet, color);


                    //Calls
                    Drawable icon_calls = ContextCompat.getDrawable(contextglobal, R.drawable.ic_local_phone_white_48dp);
                    Bitmap b_icon_calls = ((BitmapDrawable) icon_calls).getBitmap();
                    b_icon_calls = colorize(b_icon_calls, color);


                    //SMS
                    Drawable icon_sms = ContextCompat.getDrawable(contextglobal, R.drawable.ic_message_white_48dp);
                    Bitmap b_icon_sms = ((BitmapDrawable) icon_sms).getBitmap();
                    b_icon_sms = colorize(b_icon_sms, color);


                    PackageManager m = contextglobal.getPackageManager();
                    String s = contextglobal.getPackageName();
                    PackageInfo p = m.getPackageInfo(s, 0);
                    s = p.applicationInfo.dataDir;

                    ArrayList<String> values = new ArrayList<String>();


                    String filename = s + "/files/inet" + Integer.toString(i) + ".png";
                    Log.d(LOG_TAG, filename);
                    out = new FileOutputStream(filename);
                    b_icon_inet.compress(Bitmap.CompressFormat.PNG, 100, out);

                    filename = s + "/files/calls" + Integer.toString(i) + ".png";
                    Log.d(LOG_TAG, filename);
                    out = new FileOutputStream(filename);
                    b_icon_calls.compress(Bitmap.CompressFormat.PNG, 100, out);

                    filename = s + "/files/sms" + Integer.toString(i) + ".png";
                    Log.d(LOG_TAG, filename);
                    out = new FileOutputStream(filename);
                    b_icon_sms.compress(Bitmap.CompressFormat.PNG, 100, out);
                }




                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "";
        }

    }
}

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

/**
 * Created by hepolise on 25.03.17.
 */


public class Colorize {
    Context contextglobal;
    String LOG_TAG = "cellLogs";
    String d_id;



    public String StartColorize(Context ctx, String profile_id) {

        contextglobal = ctx;
        d_id = profile_id;
        new ColorizeBitmap().execute();
        return "";
    }
    public Bitmap colorize(Bitmap srcBmp, int dstColor) {
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
    class ColorizeBitmap extends AsyncTask<Integer, String, String> {


        @Override
        public  String doInBackground(Integer... id) {

            FileOutputStream out = null;
            try {
                SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(contextglobal);
                int color =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);

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


                String filename = s + "/files/inet" + d_id + ".png";
                Log.d(LOG_TAG, filename);
                out = new FileOutputStream(filename);
                b_icon_inet.compress(Bitmap.CompressFormat.PNG, 100, out);

                filename = s + "/files/calls" + d_id + ".png";
                Log.d(LOG_TAG, filename);
                out = new FileOutputStream(filename);
                b_icon_calls.compress(Bitmap.CompressFormat.PNG, 100, out);

                filename = s + "/files/sms" + d_id + ".png";
                Log.d(LOG_TAG, filename);
                out = new FileOutputStream(filename);
                b_icon_sms.compress(Bitmap.CompressFormat.PNG, 100, out);
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

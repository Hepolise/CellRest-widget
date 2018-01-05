package ru.hepolise.cellrest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Hepolise on 05.01.2018.
 */

public class Utils {
    static String L = "cellLogs";
    static void copyFile(String inputPath, String inputFile, String outputFile) {

        InputStream in;
        OutputStream out;
        try {

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(inputPath + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e(L, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(L, e.getMessage());
        }

    }
    static void restartApp(Context c) {
        Log.d(L, "Exiting...");
        Intent mStartActivity = new Intent(c, SettingsActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(c, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
        System.exit(0);
    }
    static void deleteFile(String inputPath, String inputFile) {
        try {
            Log.d(L, "del file: " + inputPath + inputFile);
            // delete the original file
            Boolean res = new File(inputPath + inputFile).delete();
            Log.d(L, "res delete: " + res);
        } catch (Exception e) {
            Log.e(L, e.getMessage());
        }
    }
    static void clearFile(String prefs, Context c) {
        Log.d(L, "clearing: " + prefs);
        SharedPreferences sharedPreferences = c.getSharedPreferences(prefs, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Deleted_prefs", "this");
        editor.clear();
        editor.commit();
    }
}

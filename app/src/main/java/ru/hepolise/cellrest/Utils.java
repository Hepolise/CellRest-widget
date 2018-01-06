package ru.hepolise.cellrest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

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
    static ArrayList genList(Context c) {
        //saveSettings(c);
        ArrayList<String> values = new ArrayList<String>();
        String login;
        SharedPreferences sh;
        SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        int length = sharedPreferences.getInt("length", 1);
        String loaded_prefs = sharedPreferences.getString("loaded_prefs", "prefs_0");
        Log.d(L, "loaded_prefs: " + loaded_prefs);
        Log.d(L, "length: " + length);

        long ts;
//        PackageManager m = c.getPackageManager();
//        String s = c.getPackageName();
//        PackageInfo p = null;
//        String prefsFile;
        for (int i=0; i<length; i++) {
            try {
                //p = m.getPackageInfo(s, 0);
                ts = sharedPreferences.getLong(Integer.toString(i), 0);
                Log.d(L, "TS: " + ts);
                Log.d(L, "i: " + i);
//                prefsFile = p.applicationInfo.dataDir + "/shared_prefs/prefs_" + Long.toString(ts) + ".xml";
//                Log.d(L, "File path: " +  prefsFile);
//                File prefs = new File(prefsFile);
                if (loaded_prefs.equals("prefs_" + Long.toString(ts))) { // cause we are not able to load new prefs without restarting app
                    Log.d(L, "This is loaded prefs");
                    sh = PreferenceManager.getDefaultSharedPreferences(c);
                } else {
                    sh = c.getSharedPreferences("prefs_" + Long.toString(ts), MODE_PRIVATE);
                }
                login = sh.getString(QuickstartPreferences.login, "");
                Log.d(L, "login: " + login);
                values.add(i, login);
            } catch (Exception e) {
                Log.e (L, e.getMessage());
            }
        }
        return values;
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
    static private void saveSettings(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        String prefs_id = sharedPreferences.getString("loaded_prefs", "prefs_0");

        //copy file
        try {
            PackageManager m = c.getApplicationContext().getPackageManager();
            String s = c.getApplicationContext().getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            copyFile(p.applicationInfo.dataDir + "/shared_prefs/" ,c.getApplicationContext().getPackageName() + "_preferences.xml", prefs_id + ".xml" );
        } catch (Exception e) {
            Log.d (L, e.getMessage());
        }
    }

    static void switchTo(long ts, Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        SettingsActivity.fa.finishAffinity();

        saveSettings(c);


        sharedPreferences.edit().putString("loaded_prefs", "prefs_" + Long.toString(ts)).commit();
        //copy file
        try {
            PackageManager m = c.getApplicationContext().getPackageManager();
            String s = c.getApplicationContext().getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            Utils.copyFile(p.applicationInfo.dataDir + "/shared_prefs/", "prefs_" + Long.toString(ts) + ".xml", c.getApplicationContext().getPackageName() + "_preferences.xml");
        } catch (Exception e) {
            Log.d (L, e.getMessage());
        }
        restartApp(c.getApplicationContext());
    }
}

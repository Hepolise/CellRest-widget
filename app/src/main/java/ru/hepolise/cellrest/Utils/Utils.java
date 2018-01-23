package ru.hepolise.cellrest.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.SettingsActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hepolise on 05.01.2018.
 */

public class Utils {
    static String L = "cellLogs";
    public static void copyFile(String inputPath, String inputFile, String outputFile) {

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
    public static void deletePrefs(String prefs, Context c) {
        try {
            PackageManager m = c.getPackageManager();
            String s = c.getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            Log.d(L, "del file: " + p.applicationInfo.dataDir + "/shared_prefs/" + prefs + ".xml");
            // delete the original file
            Boolean res = new File(p.applicationInfo.dataDir + "/shared_prefs/" + prefs + ".xml").delete();
            Log.d(L, "res delete: " + res);
        } catch (Exception e) {
            Log.e(L, e.getMessage());
        }
    }
    public static void clearFile(String prefs, Context c) {
        Log.d(L, "clearing: " + prefs);
        SharedPreferences sharedPreferences = c.getSharedPreferences(prefs, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString("Deleted_prefs", "this")
                .clear()
                .commit();
    }
    public static ArrayList genList(Context c) {
        ArrayList<String> values = new ArrayList<String>();
        String login;
        SharedPreferences sh;
        SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        int length = sharedPreferences.getInt("length", -1); // cannot be default here
        if (length != -1) {
            String loaded_prefs = sharedPreferences.getString("loaded_prefs", "prefs_0");
            Log.d(L, "loaded_prefs: " + loaded_prefs);
            Log.d(L, "length: " + length);

            long ts;
            for (int i = 0; i < length; i++) {
                try {
                    ts = sharedPreferences.getLong(Integer.toString(i), 0);
                    Log.d(L, "TS: " + ts);
                    Log.d(L, "i: " + i);
                    if (loaded_prefs.equals("prefs_" + Long.toString(ts))) { // cause we are not able to load new prefs without restarting app
                        Log.d(L, "This is loaded prefs");
                        sh = PreferenceManager.getDefaultSharedPreferences(c);
                        String active = c.getString(R.string.active);
                        login = sh.getString(QuickstartPreferences.login, "") + " (" + active + ")";
                    } else {
                        sh = c.getSharedPreferences("prefs_" + Long.toString(ts), MODE_PRIVATE);
                        login = sh.getString(QuickstartPreferences.login, "");
                    }
                    Log.d(L, "login: " + login);
                    values.add(i, login);
                } catch (Exception e) {
                    Log.e(L, e.getMessage());
                }
            }
            return values;
        } else {
            Log.e(L + " Chooser", "length is -1");
            return null;
        }
    }
    public static void restartApp(Context c) {
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

    public static void switchTo(final long timestamp, final Context c, final Boolean cancelable, final Boolean saveSettings) {
        AlertDialog.Builder ad = new AlertDialog.Builder(c);
        ad.setTitle(c.getString(R.string.switcher_dialog_remove_title));
        ad.setMessage(c.getString(R.string.switcher_dialog_remove_message));
        ad.setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                long ts = timestamp;
                SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
                SettingsActivity.fa.finishAffinity();
                if (saveSettings) {
                    saveSettings(c);
                    Log.d(L, "Save settings: true");
                }



                if (ts == 0) {
                    Log.d(L, "TS is null, Switching to the first account");
                    ts = sharedPreferences.getLong(Integer.toString(0), 0);
                }
                Log.d(L, "Switching to: " + ts);

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
        });
        if (cancelable) {
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                }
            });
            ad.setNegativeButton(c.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            });
        }
        ad.setCancelable(cancelable);
        ad.show();
    }
    public static void addUser(Context c, Boolean saveSettings) {
        long ts = System.currentTimeMillis();
        SharedPreferences myPrefs = c.getSharedPreferences("prefs_" + Long.toString(ts), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor;
        prefsEditor = myPrefs.edit();
//strVersionName->Any value to be stored
        prefsEditor.putString("thisPrefs", "pref:" + Long.toString(ts)); // do not delete
        prefsEditor.commit();
        SharedPreferences sharedPreferences = c.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        int length = sharedPreferences.getInt("length", 0);
        sharedPreferences.edit()
                .putInt("length", length + 1)
                .putLong(Integer.toString(length), ts)
                .commit();
        switchTo(ts, c, false, saveSettings);
    }
    static public boolean checkIntroComplete(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MainPrefs", MODE_PRIVATE);
        return (sharedPreferences.getBoolean(QuickstartPreferences.intro_done, false));
    }

}

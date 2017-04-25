package ru.hepolise.cellrest;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Account extends ListActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//

//    }

    private void copyFile(String inputPath, String inputFile, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
//            File dir = new File (outputPath);
//            if (!dir.exists())
//            {
//                dir.mkdirs();
//            }


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
            Log.e("cellLogs", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("cellLogs", e.getMessage());
        }

    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_account);
        String[] values = new String[] { "first", "second", "third" };

        final int len = values.length;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), Integer.toString(len), Toast.LENGTH_SHORT).show();
                SharedPreferences myPrefs = getSharedPreferences("prefs_" + Integer.toString(len), MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor;
                prefsEditor = myPrefs.edit();
//strVersionName->Any value to be stored
                prefsEditor.putString("CHECKVALUE", "HEH");
                prefsEditor.commit();
                SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
                sharedPreferences.edit().putString("wokring_prefs", "prefs_" + Integer.toString(len)).commit();
                SettingsActivity.fa.finish();



                //copy file
                try {
                    PackageManager m = getApplicationContext().getPackageManager();
                    String s = getApplicationContext().getPackageName();
                    PackageInfo p = m.getPackageInfo(s, 0);
                    copyFile(p.applicationInfo.dataDir + "/shared_prefs/", "prefs_" + Integer.toString(len) + ".xml", getApplicationContext().getPackageName() + "_preferences.xml");
                } catch (Exception e) {
                    Log.d ("cellLogs", e.getMessage());
                }



//                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                startActivity(intent);
                Intent mStartActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, Integer.toString(position) + " выбран", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putString("wokring_prefs", "prefs_" + Integer.toString(position)).commit();
        SettingsActivity.fa.finish();
        //TODO: moving prefs_* to default prefs

    }
}



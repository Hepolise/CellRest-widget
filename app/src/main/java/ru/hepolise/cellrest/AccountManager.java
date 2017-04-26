package ru.hepolise.cellrest;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.ArrayList;

public class AccountManager extends ListActivity {

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


    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        String prefs_id = sharedPreferences.getString("working_prefs", "prefs_0");
        //SettingsActivity.fa.finish();



        //copy file
        try {
            PackageManager m = getApplicationContext().getPackageManager();
            String s = getApplicationContext().getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            copyFile(p.applicationInfo.dataDir + "/shared_prefs/" ,getApplicationContext().getPackageName() + "_preferences.xml", prefs_id + ".xml" );
        } catch (Exception e) {
            Log.d ("cellLogs", e.getMessage());
        }
    }

    private void restartApp(int pos) {
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        SettingsActivity.fa.finishAffinity();

        saveSettings();


        sharedPreferences.edit().putString("working_prefs", "prefs_" + Integer.toString(pos)).commit();
        //copy file
        try {
            PackageManager m = getApplicationContext().getPackageManager();
            String s = getApplicationContext().getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            copyFile(p.applicationInfo.dataDir + "/shared_prefs/", "prefs_" + Integer.toString(pos) + ".xml", getApplicationContext().getPackageName() + "_preferences.xml");
        } catch (Exception e) {
            Log.d ("cellLogs", e.getMessage());
        }



        Log.d("cellLogs", "Exiting...");
        Intent mStartActivity = new Intent(getApplicationContext(), SettingsActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
        System.exit(0);
    }


    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_account);
        saveSettings();

        //String[] values = new String[] {  "1"};
        ArrayList<String> values = new ArrayList<String>();
        String login;
        SharedPreferences sh;
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        //account in shared_prefs is amount of accounts
        int accounts = sharedPreferences.getInt("account", 0);
        String working_prefs = sharedPreferences.getString("working_prefs", "prefs_0");
        for (int i=0; i<=accounts; i++) {
            Log.d("cellLogs", working_prefs);
            if (working_prefs.equals("prefs_" + Integer.toString(i))) {
                Log.d("cellLogs", "default prefs");
                sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            } else {
                Log.d("cellLogs", "custom prefs");
                sh = getSharedPreferences("prefs_" + Integer.toString(i), MODE_PRIVATE);
            }
            login = sh.getString(QuickstartPreferences.login, "");
            Log.d("cellLogs", login + " " + Integer.toString(i));
            values.add(i, login);
        }
        final int len = values.size();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int deleting, long deleting_long) {











                final Context context = AccountManager.this;
                ///TODO: move to strings
                String title = "Удалить аккаунт?";
                String message = "Вы действительно хотите удалить аккаунт?";
                String button1String = "Да";
                String button2String = "Нет";

                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setTitle(title);  // заголовок
                ad.setMessage(message); // сообщение
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        String login;
                        SharedPreferences sh;
                        ArrayList<String> values = new ArrayList<String>();
                        Toast.makeText(context, "Удаление...",
                                Toast.LENGTH_LONG).show();

                        //delete
                        try {
                            PackageManager m = getApplicationContext().getPackageManager();
                            String s = getApplicationContext().getPackageName();
                            PackageInfo p = m.getPackageInfo(s, 0);
                            deleteFile(p.applicationInfo.dataDir + "/shared_prefs/", "prefs_" + Integer.toString(deleting) + ".xml");
                        } catch (Exception e) {
                            Log.d ("cellLogs", e.getMessage());
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
                        int accounts = len - 2;
                        sharedPreferences.edit().putInt("account", accounts).apply();
                        Log.d("cellLogs", Integer.toString(accounts));
                        String working_prefs = sharedPreferences.getString("working_prefs", "prefs_0");
                        for (int i=0; i<=accounts; i++) {
                            Log.d("cellLogs", working_prefs);
                            if (working_prefs.equals("prefs_" + Integer.toString(i))) {
                                Log.d("cellLogs", "default prefs");
                                sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            } else {
                                Log.d("cellLogs", "custom prefs");
                                sh = getSharedPreferences("prefs_" + Integer.toString(i), MODE_PRIVATE);
                            }
                            login = sh.getString(QuickstartPreferences.login, "");
                            Log.d("cellLogs", login + " " + Integer.toString(i));
                            values.add(i, login);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, values);
                        setListAdapter(adapter);
                    }
                });
                ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
//                        Toast.makeText(context, "Отмена", Toast.LENGTH_LONG)
//                                .show();
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
//                        Toast.makeText(context, "Отмена",
//                                Toast.LENGTH_LONG).show();
                    }
                });
                ad.show();















                return true;
            }
        });


        //fab
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
                sharedPreferences.edit().putInt("account", len).apply();
                restartApp(len);

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        restartApp(position);
    }


}



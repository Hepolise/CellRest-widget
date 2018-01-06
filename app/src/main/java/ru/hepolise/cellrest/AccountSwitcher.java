package ru.hepolise.cellrest;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by hepolise on 06.01.18.
 */

public class AccountSwitcher  extends ListActivity {
    String L = "cellLogs";
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // view
        setContentView(R.layout.activity_account);

        // list
        ArrayList values = Utils.genList(getApplicationContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        // length
        final SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        final int length = sharedPreferences.getInt("length", 1);


        // switcher
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int deleting, long deleting_long) {
                final Context context = getApplicationContext();
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
                        ArrayList<String> values = new ArrayList<String>();
                        //Toast.makeText(context, "Удаление...",
                        //Toast.LENGTH_LONG).show();

                        Log.d(L, "deleting id: " + deleting);
                        sharedPreferences.edit().putInt("length", length - 1).commit();
                        Log.d(L, "New length: " + Integer.toString(length - 1));

                        String loaded_prefs = sharedPreferences.getString("loaded_prefs", "prefs_0");
                        if (deleting != length - 1) { // if the amount of accounts
                            Log.d(LOG_TAG, "deleting is less than accounts, moving up");
                            int i;
                            for (i = deleting + 1; i < accounts; i++) {
                                Log.d(LOG_TAG, "Moving: " + i);
                                try {
                                    PackageManager m = getApplicationContext().getPackageManager();
                                    String s = getApplicationContext().getPackageName();
                                    PackageInfo p = m.getPackageInfo(s, 0);
                                    Utils.copyFile(p.applicationInfo.dataDir + "/shared_prefs/",
                                            "prefs_" + i + ".xml",
                                            "prefs_" + (i - 1) + ".xml");
                                } catch (Exception e) {
                                }
                            }
                            Utils.clearFile("prefs_" + i, getApplicationContext());
                        }
                        if (working_prefs.equals("prefs_" + Integer.toString(deleting))) {
                            Log.d (LOG_TAG, "working prefs = deleting id");
                            switchTo(deleting - 1);
                        }

                        values = Utils.genList(getApplicationContext());


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, values);
                        setListAdapter(adapter);
                    }
                });
                ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {}
                });
                ad.setCancelable(true);
                ad.show();
                return true;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Utils.switchTo(position, getApplicationContext());
    }



        // fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long ts = System.currentTimeMillis();
                SharedPreferences myPrefs = getSharedPreferences("prefs_" + Long.toString(ts), MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor;
                prefsEditor = myPrefs.edit();
//strVersionName->Any value to be stored
                prefsEditor.putString("thisPrefs", "pref:" + Long.toString(ts));
                prefsEditor.commit();
                SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
                sharedPreferences.edit()
                        .putInt("length", length + 1)
                        .putLong(Integer.toString(length), ts)
                        .commit();
                Utils.switchTo(ts, getApplicationContext());
            }
        });
    }
}

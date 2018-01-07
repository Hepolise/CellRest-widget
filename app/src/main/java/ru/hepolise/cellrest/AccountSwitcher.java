package ru.hepolise.cellrest;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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


        // switcher
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int deleting, long deleting_long) {
                final Context context = AccountSwitcher.this;
                ///TODO: move to strings
                String title = "Удалить аккаунт?";
                String message = "Вы действительно хотите удалить аккаунт?";
                String button1String = "Да";
                String button2String = "Нет";
                //long ts;

                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        ArrayList<String> values;
                        long ts;


                        Log.d(L, "deleting id: " + deleting);
                        sharedPreferences.edit().putInt("length", length - 1).commit();
                        Log.d(L, "New length: " + Integer.toString(length - 1));

                        // deleting
                        ts = sharedPreferences.getLong(Integer.toString(deleting), 0);
                        long ts_del = sharedPreferences.getLong(Integer.toString(deleting), 0);
                        sharedPreferences.edit().remove(Integer.toString(deleting)).commit();
                        Utils.clearFile("prefs_" + Long.toString(ts), getApplicationContext());

                        String loaded_prefs = sharedPreferences.getString("loaded_prefs", "prefs_0");
                        if (deleting != length - 1) { // if we delete not last account
                            Log.d(L, "deleting is less than accounts, moving up");
                            int i;
                            for (i = deleting + 1; i < length; i++) {
                                Log.d(L, "Moving: " + i);
                                ts = sharedPreferences.getLong(Integer.toString(i), 0);
                                sharedPreferences.edit().putLong(Integer.toString(i-1), ts).commit();
                            }
                            Log.d(L, "Deleting last ID from prefs: " + (i - 1));
                            sharedPreferences.edit().remove(Integer.toString(i - 1)).commit();
                        }



                        if (loaded_prefs.equals("prefs_" + Long.toString(ts_del))) {
                            Log.d (L, "working prefs = deleting id");
                            long t = sharedPreferences.getLong(Integer.toString(deleting - 1), 0);
                            Utils.switchTo(t, getApplicationContext());
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
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {}
                });
                ad.show();
                return true;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        long ts = sharedPreferences.getLong(Integer.toString(position), 0);
        Utils.switchTo(ts, getApplicationContext());
    }

}

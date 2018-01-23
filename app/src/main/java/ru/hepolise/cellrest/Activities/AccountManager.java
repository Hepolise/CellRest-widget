package ru.hepolise.cellrest.Activities;

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

import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.Utils.Utils;

/**
 * Created by hepolise on 06.01.18.
 */

public class AccountManager  extends ListActivity {
    String L = "cellLogs";
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // view
        setContentView(R.layout.activity_account_manager);

        setList();

        // length
        final SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        final int length = sharedPreferences.getInt("length", 1);

        // Activity Context
        final Context activityContext = this;

        // fab
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.addUser(activityContext, true);
                setList();
            }
        });


        // switcher
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int deleting, long deleting_long) {
                final Context context = AccountManager.this;
                ///TODO: move to strings
                String title = context.getString(R.string.manager_dialog_remove_title);
                String message = context.getString(R.string.manager_dialog_remove_message);
                String button1String = context.getString(R.string.yes);
                String button2String = context.getString(R.string.no);
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
//                        int textWidgetId = sharedPreferences.getInt("WidgetText_by_ts_" + ts, 0);
//                        int widgetId = sharedPreferences.getInt("TraffWidget_by_ts_" + ts, 0);
//                        sharedPreferences.edit()
//                                .remove(Integer.toString(deleting))
//                                .remove("WidgetText_by_ts_" + ts)
//                                .remove("TraffWidget_by_ts_" + ts)
//                                .remove("widget_id_"+textWidgetId)
//                                .remove("widget_id_"+widgetId)
//                                .commit();
                        //Utils.clearFile("prefs_" + Long.toString(ts), getApplicationContext());
                        Utils.deletePrefs("prefs_" + Long.toString(ts), getApplicationContext());

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
                            if (deleting == 0) {
                                Log.d(L, "last user");
                                Utils.addUser(activityContext, false);
                            } else {
                                Utils.switchTo(t, activityContext, false, false);
                            }
                        }

                        setList();
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
    private void setList() {
        // list
        Log.d(L, "setList()");
        ArrayList values = Utils.genList(getApplicationContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        long ts = sharedPreferences.getLong(Integer.toString(position), 0);
        Utils.switchTo(ts, this, true, true);
    }

}

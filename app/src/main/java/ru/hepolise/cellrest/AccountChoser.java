package ru.hepolise.cellrest;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Created by hepolise on 26.04.17.
 */

public class AccountChoser extends ListActivity {






    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_account_choser);

        int id = getIntent().getIntExtra("id", 0);

        ArrayList<String> values = new ArrayList<String>();
        String login;
        SharedPreferences sh;
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
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


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);




        int appWidgetId = getIntent().getIntExtra("id", 0);

        sharedPreferences.edit().putString(Integer.toString(appWidgetId), Integer.toString(position)).commit();
        //Context context = getApplicationContext();
        Intent updateIntent = new Intent(getApplicationContext(), TraffWidget.class);
        updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        getApplicationContext().sendBroadcast(updateIntent);
        finish();

    }
}

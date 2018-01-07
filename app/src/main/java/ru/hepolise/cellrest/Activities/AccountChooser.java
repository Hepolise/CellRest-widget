package ru.hepolise.cellrest.Activities;

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

import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.Utils.Utils;
import ru.hepolise.cellrest.Widgets.TraffWidget;
import ru.hepolise.cellrest.Widgets.WidgetText;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Created by hepolise on 26.04.17.
 */

public class AccountChooser extends ListActivity {

    String L = "cellLogs";




    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_account_choser);

        ArrayList<String> values = Utils.genList(getApplicationContext());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);

        int appWidgetId = getIntent().getIntExtra("id", 0);
        Log.d(L, "widget id (from activity): " + appWidgetId);
        String from = getIntent().getStringExtra("from");

        long ts = sharedPreferences.getLong(Integer.toString(position), 0);
        sharedPreferences.edit().putLong("widget_id_"+Integer.toString(appWidgetId), ts).commit();
        //Context context = getApplicationContext();
        Intent updateIntent;
        if (from.equals("WidgetText")) {
             updateIntent = new Intent(getApplicationContext(), WidgetText.class);
        } else {
            updateIntent = new Intent(getApplicationContext(), TraffWidget.class);
        }

        updateIntent.setAction(ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { appWidgetId });
        getApplicationContext().sendBroadcast(updateIntent);
        finish();

    }
}

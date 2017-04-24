package ru.hepolise.cellrest;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Account extends ListActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//

//    }

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
                SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                shrpr.edit().putString("wokring_prefs", "prefs_" + Integer.toString(len)).commit();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, Integer.toString(position) + " выбран", Toast.LENGTH_LONG).show();
        SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        shrpr.edit().putString("wokring_prefs", "prefs_" + Integer.toString(position)).commit();
    }
}



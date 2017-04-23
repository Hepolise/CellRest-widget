package ru.hepolise.cellrest;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
//import android.support.v7;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yandex.metrica.YandexMetrica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import yuku.ambilwarna.AmbilWarnaDialog;

import static ru.hepolise.cellrest.R.string.pref_account_login;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //String stringPref = preference.toString();
            //Log.d("traffLog", "Changed " + stringPref + " to " + stringValue);


            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        //Log.d("traff", "clicked");

        // Trigger the listener immediately with the preference's
        // current value.
        //Log.d("traff", preference.toString());
        if (preference.toString().equals(preference.getContext().getString(R.string.pin_code_title))) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), preference.getContext().getString(R.string.pin_code_desc)));
        } else if (preference.toString().equals(preference.getContext().getString(R.string.pref_account_login))){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), preference.getContext().getString(R.string.pref_desc_login)));
        } else if (preference.toString().equals(preference.getContext().getString(R.string.widget_settings_color_text))){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), preference.getContext().getString(R.string.widget_settings_color_text_desc)));
        } else if (preference.toString().equals(preference.getContext().getString(R.string.pref_locale))){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            //set default desc to null
                            .getString(preference.getKey(), ""));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean intro = checkIntroComplete(this);
        Intent intent;
        if (!intro) {
            intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }


        //setupActionBar();
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            // Инициализация AppMetrica SDK
            YandexMetrica.activate(getApplicationContext(), "b7dc00b5-0eba-48e4-b2df-6ce98c35ccc0");
            // Отслеживание активности пользователей
            YandexMetrica.enableActivityAutoTracking(this.getApplication());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 1, 0, getString(R.string.help_title));


        menu.add(0, 2, 0, getString(R.string.about_title));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        String id = String.valueOf(item.getItemId());
        if (id.equals("1")) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
        if (id.equals("2")) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(this, AccountManager.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }



    private boolean checkIntroComplete(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return (sharedPreferences.getBoolean(QuickstartPreferences.intro_done, false));
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);


    }


    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || ColorPickerPreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        String LOG_TAG="celllogs";
        class ProgressTask extends AsyncTask<String, Void, String> {
            String content = "";
            @Override
            public String doInBackground(String... path) {

                try {
                    getContent(getActivity());
                } catch (IOException ex) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result){
                    Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
            }

            private String getContent(Context ctx) throws IOException {
                BufferedReader reader;
                SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(ctx);
                String pass;
                String UPD;
                String version = "";
                String token = shrpr.getString(QuickstartPreferences.TOKEN, "");
                String login = shrpr.getString(QuickstartPreferences.login, "");
                String op = shrpr.getString(QuickstartPreferences.op_list, "");
                String pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
                String return_ = shrpr.getString(QuickstartPreferences.return_, "calc");
                String android_id = "";
                Locale currentLocale = Locale.getDefault();
                String locale = currentLocale.toString();
                String loc = shrpr.getString(QuickstartPreferences.loc, "def");
                if (loc.equals("def")) {
                    loc = locale;
                }
                String tz = TimeZone.getDefault().getID();
                if (login.startsWith("+7")) {
                    login = login.substring(2);
                    //Log.d(LOG_TAG, "+7 change: " + login);
                } else if (login.startsWith("7") || login.startsWith("8")){
                    login = login.substring(1);
                    //Log.d(LOG_TAG, "7/8 change: " + login);
                }

                if (op.equals("tele2")) {
                    login = "7" + login;
                    pin_code = shrpr.getString(QuickstartPreferences.pin_code, "");
                    pass = "null";
                    android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                    //Log.d(LOG_TAG, "android_id " + android_id);
                } else {
                    pass = shrpr.getString(QuickstartPreferences.pass, "");
                }
                if (login.equals("") || pass.equals("")) {
                    UPD = "0";
                } else {
                    UPD = "1";
                }

                int versionCode = BuildConfig.VERSION_CODE;
                version = Integer.toString(versionCode);
                try {
                    URL url = new URL("https://srvr.tk/traf.php?cmd=widget&upd=" + UPD +
                            "&login=" + URLEncoder.encode(login, "UTF-8") +
                            "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                            "&op=" + URLEncoder.encode(op, "UTF-8") +
                            "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                            "&pin=" + URLEncoder.encode(pin_code, "UTF-8") +
                            "&loc=" + URLEncoder.encode(loc, "UTF-8") +
                            "&version=" + URLEncoder.encode(version, "UTF-8") +
                            "&token=" + URLEncoder.encode(token, "UTF-8") +
                            "&return=" + URLEncoder.encode(return_, "UTF-8") +
                            "&tz=" + URLEncoder.encode(tz, "UTF-8")
                            //+ "&test"
                    );
                    Log.d(LOG_TAG, "URL: " + url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder buf = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line + " ");
                    }
                    String buffer = buf.toString();
                    buffer = buffer.replace(" NEWLINE ", "\n");
                    content = buffer;
                    return (buffer);

                } catch (IOException e) {
                    if (e.getMessage().equals("No route to host")) {
                        content = ctx.getString(R.string.server_down);
                        return ctx.getString(R.string.server_down);
                    } else {
                        content = e.getMessage();
                        return e.getMessage();
                    }
                }
            }
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            Preference button = (Preference)findPreference(getString(R.string.button));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                    new ProgressTask().execute();
                    return true;
                }
            });
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("login"));
            bindPreferenceSummaryToValue(findPreference("op_list"));
            bindPreferenceSummaryToValue(findPreference("pin_code"));
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ColorPickerPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_widgetview);
            setHasOptionsMenu(true);
            final Context ctx = getActivity();
            final SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(ctx);
            Preference button = (Preference)findPreference(getString(R.string.button_colorpicker));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    int initialColor =  shrpr.getInt(QuickstartPreferences.color, 0xffffffff);
                    String color_text = shrpr.getString(QuickstartPreferences.color_text, "null");
                    if (!color_text.equals("null")){
                        int color;
                        try {
                            color = Color.parseColor(color_text);
                            shrpr.edit().putInt(QuickstartPreferences.color, color).apply();
                        } catch (IllegalArgumentException e) {
                            color = 0xffffffff;
                            String hexColor = String.format("#%06X", (0xFFFFFF & color));
                            shrpr.edit().putString(QuickstartPreferences.color_text, hexColor).apply();
                        }
                        initialColor = color;
                    }
                    AmbilWarnaDialog dialog = new AmbilWarnaDialog(ctx, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            // color is the color selected by the user.
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(QuickstartPreferences.color, color).apply();
                            String hexColor = String.format("#%06X", (0xFFFFFF & color));
                            editor.putString(QuickstartPreferences.color_text, hexColor).apply();
                            bindPreferenceSummaryToValue(findPreference("color_text"));
                            new Colorize().StartColorize(ctx);
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {}


                    });
                    dialog.show();
                    return true;
                }
            });
            bindPreferenceSummaryToValue(findPreference("font"));
            bindPreferenceSummaryToValue(findPreference("color_text"));
            bindPreferenceSummaryToValue(findPreference("loc"));
        }

    }


}

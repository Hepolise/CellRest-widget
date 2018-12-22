package ru.hepolise.cellrest;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import ru.hepolise.cellrest.Activities.AboutActivity;
import ru.hepolise.cellrest.Activities.AccountManager;
import ru.hepolise.cellrest.Activities.AppCompatPreferenceActivity;
import ru.hepolise.cellrest.Activities.HelpActivity;
import ru.hepolise.cellrest.Activities.IntroActivity;
import ru.hepolise.cellrest.GCM.RegistrationIntentService;
import ru.hepolise.cellrest.Utils.Colorize;
import ru.hepolise.cellrest.Utils.QuickstartPreferences;
import ru.hepolise.cellrest.Utils.Utils;
import yuku.ambilwarna.AmbilWarnaDialog;


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
                            // set default desc to null
                            .getString(preference.getKey(), ""));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }


    }

    public static Activity fa;
    public static Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean intro = Utils.checkIntroComplete(this);
        Intent intent;
        fa = this;
        if (!intro) {
            intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        c = getApplicationContext();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 1, 0, getString(R.string.help_title));
        menu.add(0, 2, 0, getString(R.string.intro));
        menu.add(0, 3, 0, getString(R.string.about_title));
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
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
        if (id.equals("3")) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(this, AccountManager.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ColorPickerPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_widgetview);
            setHasOptionsMenu(true);
            final Context ctx = getActivity();
            final SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(ctx);
            Preference button = findPreference(getString(R.string.button_colorpicker));
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
                            new Colorize().StartColorize(ctx); //TODO
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        String LOG_TAG="celllogs";




        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);


            Preference testConn = findPreference(getString(R.string.button));
            testConn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    preference.setEnabled(false);
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, getActivity().getString(R.string.requesting));
                    Toast.makeText(getActivity(), getActivity().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                    new DownloadData().execute();
                    return true;
                }
            });

            Preference tele2Reg = findPreference(getString(R.string.tele2_reg));
            tele2Reg.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    preference.setEnabled(false);
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, getActivity().getString(R.string.requesting));
                    Toast.makeText(getActivity(), getActivity().getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
                    new Tele2Register().execute();
                    return true;
                }
            });

            Preference calc = findPreference(QuickstartPreferences.calc);

            calc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String data;
                    Boolean checked = PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true);
                    if (checked) {
                        data = preference.getContext().getString(R.string.calc_desc);
                    } else {
                        data = preference.getContext().getString(R.string.calc_desc_2);
                    }
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, data);
                    return true;
                }
            });
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference(QuickstartPreferences.login));
            bindPreferenceSummaryToValue(findPreference(QuickstartPreferences.op_list));
            String data;
            Boolean checked = PreferenceManager
                    .getDefaultSharedPreferences(calc.getContext())
                    .getBoolean(calc.getKey(), true);
            if (checked) {
                data = calc.getContext().getString(R.string.calc_desc);
            } else {
                data = calc.getContext().getString(R.string.calc_desc_2);
            }
            sBindPreferenceSummaryToValueListener.onPreferenceChange(calc, data);
        }


        class DownloadData extends AsyncTask<String, Void, String> {
            String content = "";
            @Override
            public String doInBackground(String... path) {
                try {
                    getContent(getActivity());
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getLocalizedMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result){
                try {
                    final Preference testConn = findPreference(getString(R.string.button));
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(testConn, getActivity().getString(R.string.button_desc));
                    testConn.setEnabled(true);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Preference wasn't found");
                }
                Toast.makeText(c.getApplicationContext(), content, Toast.LENGTH_LONG).show();
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
                Boolean calc = shrpr.getBoolean(QuickstartPreferences.calc, true);
                pass = shrpr.getString(QuickstartPreferences.pass, "");
                Boolean allTrafficTele2 = !shrpr.getBoolean(QuickstartPreferences.rollover_traffic_tele2, false);
                String return_;
                if (calc) {
                    return_ = "calc";
                } else {
                    return_ = "full";
                }
                String android_id = "";
                Locale currentLocale = Locale.getDefault();
                String loc = currentLocale.toString();
                String tz = TimeZone.getDefault().getID();
                if (login.startsWith("+7")) {
                    login = login.substring(2);
                    //Log.d(LOG_TAG, "+7 change: " + login);
                } else if (login.startsWith("7") || login.startsWith("8")){
                    login = login.substring(1);
                    //Log.d(LOG_TAG, "7/8 change: " + login);
                }

                if (op.equals("tele2") || op.equals(QuickstartPreferences.rostelekom)) {
                    login = "7" + login;
                    //Log.d(LOG_TAG, "android_id " + android_id);
                }
                android_id = shrpr.getString(QuickstartPreferences.androidId, Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID));
                if (login.equals("") || pass.equals("")) {
                    UPD = "0";
                } else {
                    UPD = "1";
                }

                int versionCode = BuildConfig.VERSION_CODE;
                version = Integer.toString(versionCode);
                String test = "";
                if (Utils.isReleaseTest(ctx))
                    test = "test";
                try {
                    URL url = new URL("https://" + Utils.getHost() + "/traf.php?cmd=test_conn&upd=" + UPD +
                            "&login=" + URLEncoder.encode(login, "UTF-8") +
                            "&pass=" + URLEncoder.encode(pass, "UTF-8") +
                            "&op=" + URLEncoder.encode(op, "UTF-8") +
                            "&devid=" + URLEncoder.encode(android_id, "UTF-8") +
                            "&loc=" + URLEncoder.encode(loc, "UTF-8") +
                            "&version=" + URLEncoder.encode(version, "UTF-8") +
                            "&token=" + URLEncoder.encode(token, "UTF-8") +
                            "&return=" + URLEncoder.encode(return_, "UTF-8") +
                            "&tz=" + URLEncoder.encode(tz, "UTF-8") +
                            "&allTrafficTele=" + URLEncoder.encode(allTrafficTele2.toString(), "UTF-8")
                            + "&" + test
                    );
                    Log.d(LOG_TAG, "URL: " + url);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setHostnameVerifier(Utils.hostnameVerifier);
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
                    if (e.getMessage().contains("No route to host") || e.getMessage().contains("Host unreachable")) {
                        content = ctx.getString(R.string.server_down);
                        return ctx.getString(R.string.server_down);
                    } else {
                        content = e.getMessage();
                        return e.getMessage();
                    }
                }
            }
        }
        class Tele2Register extends AsyncTask<String, Void, String> {
            String content = "";
            @Override
            public String doInBackground(String... path) {
                try {
                    getContent(getActivity());
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getLocalizedMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                try {
                    final Preference tele2Reg = findPreference(getString(R.string.tele2_reg));
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(tele2Reg, getActivity().getString(R.string.tele2_reg_desc));
                    tele2Reg.setEnabled(true);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Preference was not found");
                }
                Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
            }



            private String getContent(Context ctx) throws IOException {
                BufferedReader reader;
                SharedPreferences shrpr = PreferenceManager.getDefaultSharedPreferences(ctx);
                String version = "";
                String login = shrpr.getString(QuickstartPreferences.login, "");
                Locale currentLocale = Locale.getDefault();
                String loc = currentLocale.toString();
                if (login.startsWith("+7")) {
                    login = login.substring(2);
                    //Log.d(LOG_TAG, "+7 change: " + login);
                } else if (login.startsWith("7") || login.startsWith("8")){
                    login = login.substring(1);
                    //Log.d(LOG_TAG, "7/8 change: " + login);
                }
                login = "7" + login;


                int versionCode = BuildConfig.VERSION_CODE;
                version = Integer.toString(versionCode);
                String test = "";
                if (Utils.isReleaseTest(ctx))
                    test = "test";
                try {
                    URL url = new URL("https://" + Utils.getHost() + "/traf.php?tele2_request_pass" +
                            "&login=" + URLEncoder.encode(login, "UTF-8") +
                            "&loc=" + URLEncoder.encode(loc, "UTF-8") +
                            "&version=" + URLEncoder.encode(version, "UTF-8")
                            + "&" + test
                    );
                    Log.d(LOG_TAG, "URL: " + url);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setHostnameVerifier(Utils.hostnameVerifier);
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder buf = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line + " ");
                    }
                    String buffer = buf.toString();
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


    }

}

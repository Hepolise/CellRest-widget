package ru.hepolise.cellrest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by hepolise on 24.03.17.
 */

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making the first account the part of multi account
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        long ts = System.currentTimeMillis();
        sharedPreferences.edit().putString("loaded_prefs", "prefs_" + ts).putLong("0", ts).commit();

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        String title = getApplicationContext().getString(R.string.app_name);
        String description = getApplicationContext().getString(R.string.about_description);
        int image = R.mipmap.ic_launcher;
        int backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        title = getApplicationContext().getString(R.string.fill_obligatory);
        description = getApplicationContext().getString(R.string.fill_obligatory_desc);
        image = R.mipmap.gen_settigs;
        backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        new Colorize().StartColorize(getBaseContext());
//        title = getApplicationContext().getString(R.string.fill_obligatory);
//        description = getApplicationContext().getString(R.string.fill_obligatory_desc);
//        image = R.mipmap.other_settings;
//        backgroundColor = Color.parseColor("#303F9F");
//        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(QuickstartPreferences.intro_done, true).apply();
        finish();
        // Do something when users tap on Skip button.
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(QuickstartPreferences.intro_done, true).apply();
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.

    }
}
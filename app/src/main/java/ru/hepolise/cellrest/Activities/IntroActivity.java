package ru.hepolise.cellrest.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.yandex.metrica.YandexMetrica;

import java.util.UUID;

import ru.hepolise.cellrest.R;
import ru.hepolise.cellrest.Utils.Colorize;
import ru.hepolise.cellrest.Utils.QuickstartPreferences;

/**
 * Created by hepolise on 24.03.17.
 */

public class IntroActivity extends AppIntro2 {
    String L = "cellLogs";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(), "b7dc00b5-0eba-48e4-b2df-6ce98c35ccc0");
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this.getApplication());


//        setFadeAnimation();
        setZoomAnimation();
//        setDepthAnimation();

        setBarColor(Color.parseColor("#3F51B5"));
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        String title = getApplicationContext().getString(R.string.app_name);
        String description = getApplicationContext().getString(R.string.about_description);
        int image = R.mipmap.ic_launcher;
        int backgroundColor = Color.parseColor("#303F9F"); // Do not change colors because of
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        title = getApplicationContext().getString(R.string.fill_obligatory);
        description = getApplicationContext().getString(R.string.fill_obligatory_desc);
        image = R.drawable.general_settings;
        backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        title = getApplicationContext().getString(R.string.slide3title);
        description = getApplicationContext().getString(R.string.slide3text);
        image = R.drawable.choose_account;
        backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        title = getApplicationContext().getString(R.string.slide4title);
        description = getApplicationContext().getString(R.string.slide4text);
        image = R.drawable.error;
        backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        apply();
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        apply();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
    private void apply() {
        // making the first account the part of multi account
        SharedPreferences sharedPreferences = getSharedPreferences("MainPrefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(QuickstartPreferences.intro_done, false)) {
            Log.d(L,"Intro is not done");
            long ts = System.currentTimeMillis();
            sharedPreferences.edit()
                    .putString("loaded_prefs", "prefs_" + ts)
                    .putLong("0", ts)
                    .putInt("length", 1)
                    .putBoolean(QuickstartPreferences.intro_done, true)
                    .commit();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit()
                    .putString("thisPrefs", "pref:" + Long.toString(ts))
                    .putString("android_id", UUID.randomUUID().toString().replace("-", ""))
                    .commit();
        }
        new Colorize().StartColorize(getBaseContext());
        finish();
    }
    public void onBackPressed() {
        Log.d(L, "onBackPressed");
    }
}
package ru.hepolise.cellrest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by hepolise on 24.03.17.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        image = R.mipmap.ic_launcher;
        backgroundColor = Color.parseColor("#303F9F");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));
        //AppIntroFragment.newInstance()
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));
        /*
        TODO
        First slide: Brief description
        Second: Selecting an operator
        Third: Phone number
        Fourth: pass or PIN-code, depending by selected operator


         */
//        addSlide(Slide.newInstance(R.layout.first_slide));
//        addSlide(Slide.newInstance(R.layout.second_slide));
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean(QuickstartPreferences.intro_done, true).apply();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.

    }
}
package ru.android4life.habittracker.activity;


import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.fragment.SavingModeFragment;
import ru.android4life.habittracker.fragment.UserNameFragment;

import static ru.android4life.habittracker.utils.StringConstants.INTRO_SKIPPED;
import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;

/**
 * Created by neewy on 13.12.16.
 */

public class FirstTimeIntroActivity extends IntroActivity {

    private int slide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setFullscreen(true);

        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_first_title)
                .description(R.string.intro_first_desc)
                .image(R.drawable.first)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(true)
                .build());

        if (android.os.Build.MANUFACTURER.toLowerCase().equals("samsung")) {
            addSlide(new FragmentSlide.Builder()
                    .fragment(SavingModeFragment.newInstance())
                    .background(R.color.selectiondialogs_materialdesign500_deep_purple)
                    .backgroundDark(R.color.selectiondialogs_materialdesign500_purple)
                    .build());
        }

        addSlide(new FragmentSlide.Builder()
                .fragment(UserNameFragment.newInstance())
                .background(R.color.teal)
                .backgroundDark(R.color.tealDark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_third_title)
                .description(R.string.intro_third_desc)
                .image(R.drawable.third)
                .background(R.color.dark_gray_press)
                .backgroundDark(R.color.dark_gray)
                .build());

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //to be implemented
            }

            @Override
            public void onPageSelected(int position) {
                slide = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //to be implemented
            }
        });

        setSkipEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (slide == 0) {
            getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putBoolean(INTRO_SKIPPED, true).apply();
        } else {
            getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putBoolean(INTRO_SKIPPED, false).apply();
        }

    }
}

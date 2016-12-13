package ru.android4life.habittracker.activity;


import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.fragment.UserNameFragment;

/**
 * Created by neewy on 13.12.16.
 */

public class FirstTimeIntroActivity extends IntroActivity {

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
                .build());

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

        setSkipEnabled(false);
    }
}

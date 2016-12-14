package ru.android4life.habittracker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import ru.android4life.habittracker.R;

import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;
import static ru.android4life.habittracker.utils.StringConstants.USER_NAME;

/**
 * Created by neewy on 13.12.16.
 */

public class UserNameFragment extends SlideFragment {

    private View root;
    private SharedPreferences sharedPreferences;

    public UserNameFragment() {
    }

    public static UserNameFragment newInstance() {
        return new UserNameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user_name, container, false);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return root;
    }

    @Override
    public boolean canGoForward() {
        TextInputLayout habitNameTextInputLayout = (TextInputLayout) root.findViewById(R.id.user_name_text);
        String userName = habitNameTextInputLayout.getEditText().getText().toString().trim();
        if (userName.length() > 0) {
            sharedPreferences.edit().putString(USER_NAME, userName).apply();
            return true;
        } else {
            return false;
        }
    }

}

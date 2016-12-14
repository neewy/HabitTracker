package ru.android4life.habittracker.fragment;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.android4life.habittracker.R;

/**
 * Created by neewy on 13.12.16.
 */

public class SavingModeFragment extends Fragment {

    public static SavingModeFragment newInstance() {
        return new SavingModeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saving_mode, container, false);
        AppCompatButton savingModeButton = (AppCompatButton) root.findViewById(R.id.saving_mode_button);
        savingModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"));
                try {
                    getActivity().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Smart manager not installed on this device", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

}

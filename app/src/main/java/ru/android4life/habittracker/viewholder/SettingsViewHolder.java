package ru.android4life.habittracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ru.android4life.habittracker.R;

public class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView settingTitle;
    public TextView settingSelection;
    private SettingsListener mListener;

    public SettingsViewHolder(View itemView) {
        super(itemView);
        settingTitle = (TextView) itemView.findViewById(R.id.text1);
        settingSelection = (TextView) itemView.findViewById(R.id.text2);
        itemView.setOnClickListener(this);
    }

    public void setListener(SettingsListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.first_name))) {
            mListener.onFirstName(v);
        } else if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.primary_color))) {
            mListener.onPrimaryColor(v);
        } else if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.language))) {
            mListener.onLanguage(v);
        } else if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.about))) {
            mListener.onAbout();
        } else if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.contributors))) {
            mListener.onContributors();
        }
    }

    public interface SettingsListener {
        void onFirstName(View caller);

        void onPrimaryColor(View caller);

        void onLanguage(View caller);

        void onAbout();

        void onContributors();
    }
}
package ru.android4life.habittracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ru.android4life.habittracker.R;

public class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SettingsListener mListener;
        public TextView settingTitle;
        public TextView settingSelection;

        public SettingsViewHolder(View itemView) {
            super(itemView);
            settingTitle = (TextView) itemView.findViewById(android.R.id.text1);
            settingSelection = (TextView) itemView.findViewById(android.R.id.text2);
            itemView.setOnClickListener(this);
        }

        public void setListener(SettingsListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.primary_color))) {
                mListener.onPrimaryColor(v);
            } else if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.language))) {
                mListener.onLanguage(v);
            }
        }

        public interface SettingsListener {
            void onPrimaryColor(View caller);

            void onLanguage(View caller);
        }
    }
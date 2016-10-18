package ru.android4life.habittracker.models;

public class Setting {
        public String title;
        public String selection;

        public Setting(String title, String selection) {
            this.title = title;
            this.selection = selection;
        }

        public Setting(String title) {
            this.title = title;
        }
    }
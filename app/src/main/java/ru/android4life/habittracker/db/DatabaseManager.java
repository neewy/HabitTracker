package ru.android4life.habittracker.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by alnedorezov on 9/26/16.
 */

/**
 * Class that instantiates a helper for the creation and working with database
 * For more details see https://habrahabr.ru/post/143431/ (there it is called HelperFactory)
 */
public class DatabaseManager {

    private static DatabaseHelper databaseHelper;

    // Utility classes, which are a collection of static members, are not meant to be instantiated
    private DatabaseManager() {
        // Even abstract utility classes, which can be extended,
        // should not have public constructors.
        // Java adds an implicit public constructor to every class
        // which does not define at least one explicitly.
        // Hence, at least one non-public constructor should be defined
    }

    public static DatabaseHelper getHelper() {
        return databaseHelper;
    }

    public static void setHelper(Context context) {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}

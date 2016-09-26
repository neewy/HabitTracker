package si.modrajagoda.didi.db;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by alnedorezov on 9/27/16.
 */

public final class Constants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "habitsTracker" + DATABASE_VERSION + ".db";
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd H:mm:ss.S", Locale.ENGLISH);

    // For DAO's from dataaccessobjectp package
    public static final String DAO_ERROR = "DAO_ERROR";
    public static final String SQL_EXCEPTION_IN = "SQL Exception in";
    public static final char SPACE = ' ';
    public static final char UNDERSCORE = '_';

    // For DatabaseHelper
    public static final String DB_HELPER = "DB_HELPER";
    public static final String ERROR = "ERROR";
    public static final String ON_CREATE = "onCreate";
    public static final String ON_UPGRADE = "onUpgrade";
    public static final String CANNOT_CREATE_DATABASE = "Can't create database";
    public static final String CANNOT_DROP_DATABASES = "Can't drop databases";

    // Utility classes, which are a collection of static members, are not meant to be instantiated
    private Constants() {
        // Even abstract utility classes, which can be extended,
        // should not have public constructors.
        // Java adds an implicit public constructor to every class
        // which does not define at least one explicitly.
        // Hence, at least one non-public constructor should be defined
    }
}

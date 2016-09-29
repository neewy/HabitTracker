package ru.android4life.habittracker.db.dataaccessobjects;

import java.util.List;

/**
 * Created by alnedorezov on 9/28/16.
 */

public interface Crud {
    int create(Object item);

    int update(Object item);

    int delete(Object item);

    List findAll();

    int createOrUpdateIfExists(Object item);
}
package si.modrajagoda.didi.db.dataaccessobjects;

import java.util.List;

/**
 * Created by alnedorezov on 9/28/16.
 */

public interface ExtendedCrud extends Crud {
    int create(Object item);

    int update(Object item);

    int delete(Object item);

    List findAll();

    int createOrUpdateIfExists(Object item);

    Object findById(int id);

    Object getObjectWithMaxId();
}

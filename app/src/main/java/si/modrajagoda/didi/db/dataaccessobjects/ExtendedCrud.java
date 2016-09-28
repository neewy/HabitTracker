package si.modrajagoda.didi.db.dataaccessobjects;

import java.util.List;

/**
 * Created by alnedorezov on 9/28/16.
 */

public interface ExtendedCrud extends Crud {
    public int create(Object item);

    public int update(Object item);

    public int delete(Object item);

    public List findAll();

    public int createOrUpdateIfExists(Object item);

    public Object findById(int id);

    public Object getObjectWithMaxId();
}

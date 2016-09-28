package si.modrajagoda.didi.db.dataaccessobjects;

import java.util.List;

/**
 * Created by alnedorezov on 9/28/16.
 */

public interface ExtendedCrud extends Crud {
    Object findById(int id);

    Object getObjectWithMaxId();
}

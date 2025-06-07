// core/src/main/java/core/dao/LabWorkDao.java
package core.dao;

import core.objects.LabWork;

import java.util.List;
import java.util.Optional;

public interface LabWorkDao {
    Optional<Long> insert(LabWork lw);
    boolean        update(LabWork lw);
    boolean        delete(long id, String ownerLogin);
    List<LabWork>  fetchAll();
}

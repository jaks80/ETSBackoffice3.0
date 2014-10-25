package etsbackoffice.datalogic;

import etsbackoffice.domain.OfficeID;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface OfficeIDDao {

    public void store(List<OfficeID> officeIDs);

    public List<OfficeID> findAgtOfficeIDs(long agtID);
}

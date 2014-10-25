package etsbackoffice.datalogic;

import etsbackoffice.domain.User;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface UserDao {

     public void store(User user);

    public void delete(long userID);

    public User searchByID(long userID);

    public List<User> searchByName(String name);

    public User searchByPostCode(String postCode);

    public User searchByOID(String oid);

    public List<User> findAll();

    public List<User> findAllSummery();

    public List findUserNameList();
}

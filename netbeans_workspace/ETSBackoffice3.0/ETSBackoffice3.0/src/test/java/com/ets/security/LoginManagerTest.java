package com.ets.security;

import com.ets.settings.domain.User;
import com.ets.util.Enums;
import java.util.Map;
import javax.validation.constraints.AssertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Yusuf
 */
public class LoginManagerTest {
    
    public LoginManagerTest() {
    }

    @Test
    public void addLoginTest() {
        
        LoginManager.resetCache();
       User user = new User();       
       user.setLoginID("jaks80");
       user.setPassword("ets215");
       user.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user);
       
       Map<String, Login> loginList = LoginManager.getLoginList();
       assertNotNull(loginList);
       assertEquals(1, loginList.keySet().size());
       
       user = new User();       
       user.setLoginID("jaks80");
       user.setPassword("ets215");
       user.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user);
       
       assertNotNull(loginList);
       assertEquals(1, loginList.keySet().size());
       
       user = new User();       
       user.setLoginID("maks80");
       user.setPassword("maks215");
       user.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user);
       
       assertNotNull(loginList);
       assertEquals(2, loginList.keySet().size());
    }
    
    @Test
    public void validateLoginTest() {
       LoginManager.resetCache();
       User user = new User();       
       user.setLoginID("jaks80");
       user.setPassword("ets215");
       user.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user);
       
       Map<String, Login> loginList = LoginManager.getLoginList();
       assertNotNull(loginList);
       assertEquals(1, loginList.keySet().size());
    
       user = new User();       
       user.setLoginID("maks80");
       user.setPassword("maks215");
       user.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user);
       
       assertNotNull(loginList);
       assertEquals(2, loginList.keySet().size());
       
       User isvalid = LoginManager.validateLogin("jaks80", "ets215");
        assertNotNull(isvalid);
    }
    
    @Test
    public void valideRoleTest(){
     LoginManager.resetCache();
       User user = new User();       
       user.setLoginID("jaks80");
       user.setPassword("ets215");
       user.setUserType(Enums.UserType.SM);
       LoginManager.addLogin(user);
    
       User user1 = new User();       
       user1.setLoginID("maks80");
       user1.setPassword("maks215");
       user1.setUserType(Enums.UserType.GS);
       LoginManager.addLogin(user1);
    
       boolean result = LoginManager.valideRole(user1.getUserType().toString(),"GS");
      assertEquals(true, result);
      
      result = LoginManager.valideRole(user1.getUserType().toString(),"AD");
      assertEquals(false, result);
      
      result = LoginManager.valideRole(user1.getUserType().toString(),"SU");
      assertEquals(false, result);
        
    }
}

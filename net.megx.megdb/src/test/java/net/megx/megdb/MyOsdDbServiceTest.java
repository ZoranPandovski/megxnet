package net.megx.megdb;

import static org.junit.Assert.*;

import java.util.List;

import net.megx.megdb.myosd.MyOsdDbService;
import net.megx.megdb.myosd.MyOsdParticipantRegistration;
import net.megx.megdb.myosd.dto.MyOsdParticipantDTOImpl;
import net.megx.megdb.myosd.impl.MyOsdDbServiceImpl;

import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class MyOsdDbServiceTest {

  @Rule
  public TestDb db = new TestDb();

  // pr = participant registration
  private MyOsdParticipantRegistration prEmpty = null;
  private MyOsdParticipantRegistration prValid = null;

  private MyOsdDbService myosd = null;

  @Before
  public void setupParticipants() {
    myosd = new MyOsdDbServiceImpl();
    myosd.setSqlSessionFactory(db.getSqlSessionFactory());

    prEmpty = new MyOsdParticipantDTOImpl();

    prValid = new MyOsdParticipantDTOImpl();
    prValid.setBothEmails("test@test.de", "test@test.de");
    prValid.setUserName("renzo");
    prValid.setRawJson("{}");
    int v = 1;
    prValid.setVersion(v);
    int num = 0;
    prValid.setMyOsdId(num);
  }

  @Test
  public void isValidRegistration() {
    assertTrue("Not a valid registration", prValid.isValidRegistration());
  }

  @Test(expected = PersistenceException.class)
  public void saveEmptyParticpant() {
    myosd.saveParticipant(prEmpty);
  }

  @Test
  public void checkEmailExists() {
    String email = "test@test123.de";
    MyOsdParticipantRegistration p = myosd
        .participantByEmail(email);

    assertNotNull(p);
    assertEquals(p.getEmail(), email);
  }
  
  @Test
  public void checkEmailNotExists() {
    String email = "testesdfsdfsadfsdfsdft123.de";
    MyOsdParticipantRegistration p = myosd
        .participantByEmail(email);

    assertNull(email +": should not be in database",p);
  }
  
  
  @Test
  public void checkNameExists() {
    String name = "God";
    MyOsdParticipantRegistration p = myosd
        .participantByName(name);

    assertNotNull(p);
    assertEquals(p.getUserName(), name);
  }
  
  @Test
  public void checkNameNotExists() {
    String name = ")(*&#$(*&(((";
    MyOsdParticipantRegistration p = myosd
        .participantByName(name);

    assertNull(p);
  }
  
  @Test
  public void checkMyOSDExists() {
    int  myOsdId = 333;
    MyOsdParticipantRegistration p = myosd
        .participantByMyOsdId(myOsdId);

    assertNotNull(p);
    assertEquals(p.getMyOsdId(),myOsdId);
  }
  
  @Test
  public void checkMyOSDNotExists() {
    int  myOsdId = 0;
    MyOsdParticipantRegistration p = myosd
        .participantByMyOsdId(myOsdId);

    assertNull(p);
  }
  

  @Test
  @Ignore
  public void saveValidParticpant() {

    myosd.saveParticipant(prValid);

  }

  @Test
  public void acceptCorrectEmails() {
    MyOsdParticipantRegistration pr = new MyOsdParticipantDTOImpl();
    pr.setBothEmails("renzo@test.de", "renzo@test.de");
    assertTrue(pr.isValidEmail());
  }

  @Test
  public void doNotAcceptWrongEmails() {
    MyOsdParticipantRegistration pr = new MyOsdParticipantDTOImpl();
    pr.setBothEmails("zo@test.de", "renzo@test.de");
    assertFalse("Not same emails", pr.isValidEmail());

    pr.setBothEmails(null, "renzo@test.de");
    assertFalse("First email null", pr.isValidEmail());

    pr.setBothEmails("", "renzo@test.de");
    assertFalse("first email empty", pr.isValidEmail());

    pr.setBothEmails("renzo@test.de", null);
    assertFalse("second email null", pr.isValidEmail());

  }
}

package net.megx.ws.osd.registry;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import net.megx.test.TestServer;
import net.megx.test.categories.IntegrationTest;
import net.megx.test.categories.RESTServiceTest;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

public class OSDSampleSubmissionITCase {

  @Rule
  public TestServer ts = new TestServer();
  private String wsPrefix = "";
  private String participationPath = "/participation";

  @Before
  public void setUp() {
    this.wsPrefix = ts.getWSPrefix() + "/OSDRegistry/v1.0.0";
  }

  /**
   * Test case for: storing sample and metadata registration successful.
   * 
   * @param sample
   *          the whole sample form json.
   * @return Status.BAD_REQUEST if the sample json is empty or null.
   */
  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void successfulOSDSampleSubmission() throws IOException {

    // read json file data to String
    InputStream in = this.getClass().getClassLoader()
        .getResourceAsStream("osd-registry-sample.json");

    String json = IOUtils.toString(in);
    // create ObjectMapper instance
    // ObjectMapper jsonMapper = new ObjectMapper();

    // convert json string to object
    // Map<String, Object> data = jsonMapper.readValue(in, Map.class);

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParam("json", json)
        // .formParameters()
        .when().post(this.wsPrefix + "/sample").then().statusCode(201).log();

  }

  /**
   * Test case for: storing sample and metadata registration when json is empty.
   * 
   * @param sample
   *          the whole sample form json.
   * @return Status.BAD_REQUEST if the sample json is empty or null.
   */
  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failOSDSampleSubmission() throws IOException {

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParameter("json", "").when()
        .post(this.wsPrefix + "/sample").then().statusCode(400).log();

  }

  /**
   * Test case for: storing sample and metadata registration when json is null.
   * 
   * @param sample
   *          the whole sample form json.
   * @return Status.BAD_REQUEST if the sample json is empty or null.
   */
  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failJsonOSDSampleSubmission() throws IOException {

    String json = null;
    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParameter("json", json).when()
        .post(this.wsPrefix + "/sample").then().statusCode(400).log();

  }

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void retrieveAllOSDParticipants() {
    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.JSON).get(this.wsPrefix + "/participants")
        .then().statusCode(200).log();
  }

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void successfulOSDParticipantSubmission() throws IOException {

    InputStream in = this.getClass().getClassLoader()
        .getResourceAsStream("osd-registry-participant.json");

    String participant = IOUtils.toString(in);

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.JSON).formParam("participant", participant)
        .when().post(this.wsPrefix + "/addParticipant").then().statusCode(200)
        .log();

  }

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void successfulOSDParticipantUpdate() throws IOException {

    InputStream in = this.getClass().getClassLoader()
        .getResourceAsStream("osd-registry-participant-update.json");

    String participant = IOUtils.toString(in);

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.JSON).formParam("participant", participant)
        .when().post(this.wsPrefix + "/updateParticipant").then()
        .statusCode(200).log();

  }

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void retrieveOSDParticipant() {
    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.JSON).queryParam("id", "OSD5")
        .get(this.wsPrefix + "/getParticipant").then().statusCode(200).log();
  }

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void deleteOSDParticipant() {
    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.JSON)
        .formParam("id", "0a65e65b-996f-493a-8c40-9d6d03c42025")
        .post(this.wsPrefix + "/deleteParticipant").then().statusCode(200)
        .log();
  }

  /**
   * Test case for: storing participation successful.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void successfulParticipationSave() {

    String participationJson = "{\"contactName\":\"test\",\"contactEmail\":\"test@mail.net\"}";

    HashMap<String, Object> formParams = new HashMap<String, Object>();
    formParams.put("contactName", "Test Name");
    formParams.put("contactEmail", "test@mail.com");
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");
    formParams.put("json", participationJson);

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(201).log();

  }

  /**
   * Test case for: storing participation when contact name not provided.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failNameParticipationSave() {

    String participationJson = "{\"contactName\":\"test\",\"contactEmail\":\"test@mail.net\"}";

    HashMap<String, Object> formParams = new HashMap<String, Object>();
    formParams.put("contactEmail", "test@mail.com");
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");
    formParams.put("json", participationJson);

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(400).log();

  }

  /**
   * Test case for: storing participation when json not provided.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failMissingJsonParticipationSave() {
    HashMap<String, String> formParams = new HashMap<String, String>();
    formParams.put("contactEmail", "test@mail.com");
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(400).log();
  }

  /**
   * Test case for: storing participation when json is null.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */

  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failJsonParticipationSave() {
    HashMap<String, String> formParams = new HashMap<String, String>();
    formParams.put("contactEmail", "test@mail.com");
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(400).log();
  }

  /**
   * Test case for: storing participation when contactEmail is null.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */
  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failEmailParticipationSave() {
    HashMap<String, String> formParams = new HashMap<String, String>();
    formParams.put("contactEmail", null);
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(400).log();
  }

  /**
   * Test case for: storing participation when contactEmail is not provided.
   * 
   * @param contactName
   *          the participant name.
   * @param contactEmail
   *          the participant email.
   * @param contactAddress
   *          the participant address.
   * @param ideas
   *          the participant ideas about OSD.
   * @param contributedSamples
   *          the participant unique samples.
   * @param funding
   *          the participant funding.
   * @param participateDate
   *          the participation date.
   * @param json
   *          the whole participation form json.
   * @return Status.BAD_REQUEST if the contactName,contactEmail and json are empty or
   *         null.
   */
  @Test
  @Category({ IntegrationTest.class, RESTServiceTest.class })
  public void failEmailMissingParticipationSave() {
    HashMap<String, String> formParams = new HashMap<String, String>();
    formParams.put("contactAddress", "address 33");
    formParams.put("ideas", "ideas");
    formParams.put("contributedSamples", "unique sample");
    formParams.put("funding", "fundings");
    formParams.put("participateDate", "My participation date");

    given().filter(ResponseLoggingFilter.logResponseTo(System.out))
        .contentType(ContentType.URLENC).formParams(formParams)
        .post(this.wsPrefix + participationPath).then().statusCode(400).log();
  }
}

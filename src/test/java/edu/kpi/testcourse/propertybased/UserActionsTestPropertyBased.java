package edu.kpi.testcourse.propertybased;

import com.google.gson.JsonObject;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import edu.kpi.testcourse.bigtable.BigTableImpl;
import edu.kpi.testcourse.logic.UserActions;
import edu.kpi.testcourse.model.User;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

public class UserActionsTestPropertyBased {

  /**
   * Test methods:
   * -- 'addUrlToUrlArray' wich add new short url in 'urlList' of users
   * -- 'removeUrlFromUrlArray' wich remove short url in 'urlList' of users
   * -- 'getUrlList' return of 'urlList'
   *
   * Method create new user, create asserts: try to find user by email and check if exist user in
   * database (object of class 'BigTableImpl')
   * Finally we add 4 short url to user 'urlList' as random between 10 and 25 symbols, then delete first and third
   * and return true if in the end we have second and fourth short url in 'urlList' of user
   *
   * @throws Exception our error
   */
  @Test
  void addUrlToUrlArray_removeUrlFromUrlArray_propertyBased() throws Exception {
    String email = "admin123@gmail.com";
    String password = "qwerty123";
    ArrayList<String> urlList = new ArrayList<>();

    User user = new User(email, password, urlList);
    user.setUuid(UUID.randomUUID().toString());
    UserActions.createUser(user);

    //    DON'T WORK METHODS: signUp, signIn
    //    JSONObject jsonObject = new JSONObject();
    //    jsonObject.put("email", "admin123@gmail.com");
    //    jsonObject.put("password", "qwerty123");
    //    jsonObject.put("urlList", new ArrayList<String>());
    //
    //    usersController.signUp(jsonObject);
    //    assertThat(UserActions.findUserByEmail(email)).isEqualTo(true);
    //
    //    usersController.signIn(jsonObject);

    assertThat(UserActions.findUserByEmail(email)).isEqualTo(true);
    assertThat(UserActions.retrieveUserByEmail(email)).isEqualTo(user);

    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(10, 25),
        strings().basicLatinAlphabet().ofLengthBetween(10, 25),
        strings().basicLatinAlphabet().ofLengthBetween(10, 25),
        strings().basicLatinAlphabet().ofLengthBetween(10, 25)
      ).check((short1, short2, short3, short4) -> {
      user.addUrlToUrlArray(short1);
      user.addUrlToUrlArray(short2);
      user.addUrlToUrlArray(short3);
      user.addUrlToUrlArray(short4);

      user.removeUrlFromUrlArray(short1);
      user.removeUrlFromUrlArray(short3);

      ArrayList<String> urlListTest = new ArrayList<>();
      Collections.addAll(urlListTest, short2, short4);

      return user.getUrlList().toArray() == urlListTest.toArray();
      });
  }

  /**
   * Test methods:
   * -- 'putUser' wich put values in Map<key, JsonObject>, where 'key' is our string key and 'JsonObject' our user
   * -- 'getUser' wich using 'key' get our user ('JsonObject') from Map<key, JsonObject>
   *
   * Method check if equal our 'JsonObject' wich we put with 'JsonObject' wich we get using the same 'key'
   *
   * @throws Exception our error
   */
  @Test
  void putUser_getUser_propertyBased() throws Exception {
    JSONObject jsonObject = new JSONObject();
    JSONParser jsonParser = new JSONParser();

    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(20, 30),
        strings().basicLatinAlphabet().ofLengthBetween(20, 30),
        strings().basicLatinAlphabet().ofLengthBetween(8, 15)
      ).check((key, email, password) -> {
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("urlList", new ArrayList<String>());

      BigTableImpl bigTable = new BigTableImpl();

      try {
        bigTable.putUser(key, (JsonObject) jsonParser.parse(jsonObject.toString()));
      } catch (ParseException e) {
        e.printStackTrace();
      }

      return bigTable.getUser(key).toString().equals(jsonObject.toString());
    });
  }
}

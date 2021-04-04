package edu.kpi.testcourse.propertybased;

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
   * Finally we add 4 short url to user 'urlList' as random between 15 and 25 symbols, then delete first and third
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

    assertThat(UserActions.findUserByEmail(email)).isEqualTo(true);
    assertThat(UserActions.retrieveUserByEmail(email)).isEqualTo(user);

    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(15, 25),
        strings().basicLatinAlphabet().ofLengthBetween(15, 25),
        strings().basicLatinAlphabet().ofLengthBetween(15, 25),
        strings().basicLatinAlphabet().ofLengthBetween(15, 25)
      ).check((short1, short2, short3, short4) -> {
      user.initiateUrlList();
      user.addUrlToUrlArray(short1);
      user.addUrlToUrlArray(short2);
      user.addUrlToUrlArray(short3);
      user.addUrlToUrlArray(short4);

      user.removeUrlFromUrlArray(short1);
      user.removeUrlFromUrlArray(short3);

      ArrayList<String> urlListTest = new ArrayList<>();
      Collections.addAll(urlListTest, short2, short4);

      return user.getUrlList().toString().equals(urlListTest.toString());
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
    String email = "admin567@gmail.com";
    String password = "qwerty567";
    ArrayList<String> urlList = new ArrayList<>();

    User user = new User(email, password, urlList);
    user.setUuid(UUID.randomUUID().toString());
    UserActions.createUser(user);

    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(20, 30)
      ).check((key) -> {

      BigTableImpl bigTable = new BigTableImpl();
      bigTable.putUser(key, user.toJson());

      return bigTable.getUser(key).toString().equals(user.toJson().toString());
    });
  }
}

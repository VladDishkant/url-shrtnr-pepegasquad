package edu.kpi.testcourse.propertybased;

import edu.kpi.testcourse.logic.UserActions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

public class UserActionsTestPropertyBased {

  // Length between method put_url() property based test
  @Test
  void lengthUrls_propertyBased() throws Exception {
    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(5, 30),
        strings().basicLatinAlphabet().ofLengthBetween(5, 2048)
      ).check((shortenedUrl, fullUrl) -> {
      try {
        UserActions.putUrl(shortenedUrl, fullUrl);
      } catch (Exception e) {
        return false;
      }
      return true;
    });
  }

  // Shortened url less than full url method put_url() property based test
  @Test
  void shortenedUrl_LessThan_FullUrl_propertyBased() throws Exception {
    qt().forAll(
      strings().basicLatinAlphabet().ofLengthBetween(5, 30),
      strings().basicLatinAlphabet().ofLengthBetween(5, 2048))
      .checkAssert((shortenedUrl, fullUrl) -> assertThat(shortenedUrl.length()).isLessThan(fullUrl.length()));
  }

}

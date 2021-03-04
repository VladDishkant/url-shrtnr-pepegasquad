package edu.kpi.testcourse.rest;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

/**
 * Controller for logout.
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/users")
public class UserSignOutController {

  @Post(value = "/signout")
  public HttpResponse signOut() {
    return HttpResponse.noContent();
  }
}
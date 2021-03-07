package edu.kpi.testcourse.rest;

import com.google.gson.JsonObject;
import com.nimbusds.jose.shaded.json.JSONObject;
import edu.kpi.testcourse.Main;
import edu.kpi.testcourse.logic.UserActions;
import edu.kpi.testcourse.model.Url;
import edu.kpi.testcourse.model.User;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.lang.reflect.Array;
import java.security.Principal;
import java.util.ArrayList;

@Controller("/url")
public class UrlShortenController {

  record UrlClass(String url, String shortenedUrl) {}
  record UrlListClass(ArrayList<JsonObject> urls) {}

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Post(value = "/shorten",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON)
  public MutableHttpResponse<String> shortenUrl(@Body JSONObject object, Principal principal) {
    Url url = Main.getGson().fromJson(object.toJSONString(), Url.class);

    url.setShortenedUrl(Url.shortenUrl(Main.bigTable.getUrlId()));
    UserActions.putUrl(url.getShortenedUrl(), url.getFullUrl());

    User user = UserActions.retrieveUserByEmail(principal.getName());

    if (user == null) {
      return HttpResponse.unauthorized();
    }

    user.addUrlToUrlArray(url.getShortenedUrl());
    UserActions.updateUser(user);

    return HttpResponse.created("");
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Delete(value = "/{shortenedUrl}",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON)
  public MutableHttpResponse<String> deleteUrl(@Body String shortenedUrl, Principal principal) {
    String fullUrl = Main.bigTable.getUrl(shortenedUrl);
    User user = UserActions.retrieveUserByEmail(principal.getName());

    if (fullUrl == null) {
      return HttpResponse.notFound("Shortened url with name '"+ shortenedUrl + "' doesn't exist");
    }
    if (user == null) {
      return HttpResponse.unauthorized();
    }

    UserActions.deleteUrl(shortenedUrl);
    user.removeUrlFromUrlArray(shortenedUrl);

    return HttpResponse.noContent();
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get(value = "/",
    consumes = MediaType.APPLICATION_JSON,
    produces = MediaType.APPLICATION_JSON)
  public MutableHttpResponse<String> listUrls(Principal principal) {
    User user = UserActions.retrieveUserByEmail(principal.getName());

    if (user == null) {
      return HttpResponse.unauthorized();
    }

    ArrayList<String> shortenedUrls = user.getUrlList();

    ArrayList<JsonObject> urlList = UserActions.listUrls(shortenedUrls);

    return HttpResponse.ok(Main.getGson().toJson(new UrlListClass(urlList)));
  }
}
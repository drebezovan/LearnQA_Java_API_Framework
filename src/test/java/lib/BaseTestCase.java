package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {

    protected String getHeader(Response Response, String name){
        Headers headers = Response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name), "В ответе нет заголовка с именем " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response Response, String name){
        Map<String, String> cookies = Response.getCookies();

        assertTrue(cookies.containsKey(name), "В ответе нет cookie с именем " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response Response, String name){
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);
    }
}

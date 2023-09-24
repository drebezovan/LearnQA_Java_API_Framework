package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Создать GET-запрос")
    public Response makeGetRequest(String url){
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Создать GET-запрос с header и cookie")
    public Response makeGetRequest(String url, String header, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", header))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Создать GET-запрос с cookie")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Создать GET-запрос с header")
    public Response makeGetRequestWithHeader(String url, String header){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", header))
                .get(url)
                .andReturn();
    }

    @Step("Создать POST-запрос")
    public Response makePostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Создать PUT-запрос с header и cookie")
    public Response makePutRequest(String url, String header, String cookie, Map<String, String> editData){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", header))
                .cookie("auth_sid", cookie)
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Создать PUT-запрос")
    public Response makePutRequest(String url, Map<String, String> editData){
        return given()
                .filter(new AllureRestAssured())
                .body(editData)
                .put(url)
                .andReturn();
    }
}

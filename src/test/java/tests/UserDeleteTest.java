package tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("Тест проверяет, что система не даст удалить пользователя по ID 2")
    @DisplayName("Проверка невозможности удалить пользователя по ID 2")
    @Test
    public void testDeleteDefaultUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/2", header, cookie);

        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("Тест проверяет, что авторизованного пользователя можно удалить")
    @DisplayName("Позитивная проверка удаления авторизованного пользователя")
    @Test
    public void testDeleteJustCreatedUser(){

        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData).jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData);

        //DELETE USER
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Description("Тест проверяет, что автоматизированный пользователь не может удалить другого пользователя")
    @DisplayName("Негативная проверка редактирования неавторизованного пользователя авторизованным пользователем")
    @Test
    public void testEditNotAuthUserByOtherAuthUser(){

        //GENERATE AUTH USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        //LOGIN AUTH USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GENERATE ANOTHER USER
        Map<String, String> userDataAnotherAuth = DataGenerator.getRegistrationData();

        JsonPath responseCreateAnotherAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userDataAnotherAuth).jsonPath();

        String userId = responseCreateAnotherAuth.getString("id");

        //LOGIN ANOTHER USER
        Map<String, String> authAnotherData = new HashMap<>();
        authAnotherData.put("email", userDataAnotherAuth.get("email"));
        authAnotherData.put("password", userDataAnotherAuth.get("password"));

        Response responseGetAnotherAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login", authAnotherData);

        //DELETE ANOTHER USER BY USER
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAnotherAuth, "x-csrf-token"),
                this.getCookie(responseGetAnotherAuth, "auth_sid")
        );

        String[] expectedFields = {"id", "username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }
}

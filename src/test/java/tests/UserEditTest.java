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

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("Тест проверяет возможность редактирования авторизованного пользователя")
    @DisplayName("Позитивная проверка редактирования авторизованного пользователя")
    @Test
    public void testEditJustCreatedTest(){

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

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData
                );

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Description("Тест проверяет, что нельзя редактировать неавторизованного пользователя")
    @DisplayName("Негативная проверка редактирования неавторизованного пользователя")
    @Test
    public void testEditNotAuthUser(){

        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData).jsonPath();

        String userId = responseCreateAuth.getString("id");

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        editData
                );

        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Description("Тест проверяет, что авторизованному пользователю нельзя редактировать имя неавторизованного пользователя")
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

        //GENERATE NOT AUTH USER
        Map<String, String> userDataNotAuth = DataGenerator.getRegistrationData();

        JsonPath responseCreateNotAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userDataNotAuth).jsonPath();

        String userId = responseCreateNotAuth.getString("id");

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie,
                editData
        );

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId
        );

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("Тест проверяет невозможность редактирования почты на новый email без символа @ авторизованного пользователя")
    @DisplayName("Негативная проверка редактирования почты на новый email без символа @ авторизованного пользователя")
    @Test
    public void testEditEmailJustCreatedTest(){

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

        //EDIT
        String newEmail = DataGenerator.getRandomEmail().replace("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );

        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Description("Тест проверяет невозможность редактирования поля firstName на очень короткое значение в один символ авторизованного пользователя")
    @DisplayName("Негативная проверка редактирования поля firstName на очень короткое значение в один символ авторизованного пользователя")
    @Test
    public void testEditJustCreatedShortUser(){

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

        //EDIT
        char[] data = new char[1];
        String newName = new String(data);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData
        );

        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");
    }
}

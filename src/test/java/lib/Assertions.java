package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void assertJsonByName(Response Response, String name, int expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Значение из Json не совпадает с ожидаемым значением");
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer){
        assertEquals(expectedAnswer, Response.asString(), "Текст ответа не совпадает с ожидаемым значением");
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){
        assertEquals(expectedStatusCode, Response.getStatusCode(), "Статус код ответа не совпадает с ожидаемым значением");
    }

    public static void assertJsonHasKey(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }
}

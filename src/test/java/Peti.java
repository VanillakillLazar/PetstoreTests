import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class Peti {
    private static String requestBody = "{\n  \"id\": ,\n  \"petId\": 0,\n  \"quantity\": 0,\n  \"shipDate\": \"2021-12-22T15:27:11.407Z\",\n  \"status\": \"placed\",\n  \"complete\": true\n}";

    @Test
    public void pet_store_smoke() {

        Response responsePost = given()
                .baseUri("https://petstore.swagger.io")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/store/order")
                .then()
                .extract().response();
        System.out.println(responsePost.asString());
        assertEquals(responsePost.statusCode(), 200);
        String id = responsePost.jsonPath().getString("id");
        assertNotNull(id);

        Response responseGet = given()
                .baseUri("https://petstore.swagger.io/v2/store/order/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .extract().response();
        System.out.println(responseGet.asString());
        assertEquals(responseGet.statusCode(), 200);
        assertEquals(responsePost.jsonPath().getString("petId"), responseGet.jsonPath().getString("petId"));

        Response responseDel = given()
                .baseUri("https://petstore.swagger.io/v2/store/order/" + id)
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .extract().response();
        System.out.println(responseDel.asString());
        assertEquals(responseGet.statusCode(), 200);
    }

}



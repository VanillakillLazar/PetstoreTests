import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class PetStoreTest {

// Тест проверяет полный цикл store (размещение заказа --> проверка его в базе --> удаление заказа --> проверка его отсутсвия в базе)
    @Test
    public void pet_store_end_to_end() {


        PetStoreRequest request = new PetStoreRequest();
        request.setId(new Random().nextInt(99999999)+1);
        request.setPetId(new Random().nextInt(9999)+1);
        request.setQuantity(new Random().nextInt(20)+1);
        request.setShipDate(Instant.now().toString());
        request.setStatus(Status.PLACED.name());
        request.setComplete(Boolean.TRUE);


        Response responsePost = given()
                .baseUri("https://petstore.swagger.io")
                .contentType(ContentType.JSON)
                .body(request)
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
        assertEquals(responseDel.statusCode(), 200);
        assertEquals(responsePost.jsonPath().getString("id"), responseDel.jsonPath().getString("message"));

        Response responseGet2 = given()
                .baseUri("https://petstore.swagger.io/v2/store/order/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .extract().response();
        System.out.println(responseGet2.asString());
        assertEquals(responseGet2.jsonPath().getString("message"), "Order not found");
    }

}

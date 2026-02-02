package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  private static final String PATH = "product";

  @Test
  public void testGetAllProducts() {
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(containsString("BESTÅ"));
  }

  @Test
  public void testGetProductNotFound() {
    given()
            .when()
            .get(PATH + "/999")
            .then()
            .statusCode(404)
            .body(containsString("does not exist"));
  }

  @Test
  public void testUpdateProductNameMissing() {
    String invalidUpdate =
            """
            {
              "price": 100
            }
            """;

    given()
            .contentType("application/json")
            .body(invalidUpdate)
            .when()
            .put(PATH + "/1")
            .then()
            .statusCode(422)
            .body(containsString("Product Name was not set"));
  }

  @Test
  public void testCreateProductSuccess() {
    String body =
            """
            {
              "name": "NEW_PRODUCT",
              "price": 100,
              "stock": 5
            }
            """;

    given()
            .contentType("application/json")
            .body(body)
            .when()
            .post("product")
            .then()
            .statusCode(201)
            .body(containsString("NEW_PRODUCT"));
  }

  @Test
  public void testUpdateProductSuccess() {
    String body =
            """
            {
              "name": "UPDATED_PRODUCT",
              "price": 200,
              "stock": 10
            }
            """;

    given()
            .contentType("application/json")
            .body(body)
            .when()
            .put("product/1")
            .then()
            .statusCode(200)
            .body(containsString("UPDATED_PRODUCT"));
  }

  @Test
  public void testDeleteProductSuccess() {
    given()
            .when()
            .delete("product/1")
            .then()
            .statusCode(204);
  }

}

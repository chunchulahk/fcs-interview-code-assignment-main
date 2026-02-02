package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

    @Test
    public void testCreateStoreSuccess() {
        String body =
                """
                {
                  "name": "NEW_STORE",
                  "quantityProductsInStock": 20
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("store")
                .then()
                .statusCode(201)
                .body(containsString("NEW_STORE"));
    }

    @Test
    public void testUpdateStoreSuccess() {
        // 1️ Create store first
        String createBody =
                """
                {
                  "name": "STORE_TO_UPDATE",
                  "quantityProductsInStock": 10
                }
                """;

        int id =
                given()
                        .contentType("application/json")
                        .body(createBody)
                        .when()
                        .post("store")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        // 2 Update the store
        String updateBody =
                """
                {
                  "name": "UPDATED_STORE",
                  "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType("application/json")
                .body(updateBody)
                .when()
                .put("store/" + id)
                .then()
                .statusCode(200)
                .body(containsString("UPDATED_STORE"));
    }

    @Test
    public void testDeleteStoreSuccess() {
                String body =
                """
                {
                  "name": "STORE_TO_DELETE",
                  "quantityProductsInStock": 5
                }
                """;

        int id =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("store")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        // Delete the store
        given()
                .when()
                .delete("store/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    public void testPatchStoreSuccess() {
        //  Create store first
        String createBody =
                """
                {
                  "name": "STORE_TO_PATCH",
                  "quantityProductsInStock": 10
                }
                """;

        int id =
                given()
                        .contentType("application/json")
                        .body(createBody)
                        .when()
                        .post("store")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        String patchBody =
                """
                {
                  "name": "PATCHED_STORE",
                  "quantityProductsInStock": 99
                }
                """;

        given()
                .contentType("application/json")
                .body(patchBody)
                .when()
                .patch("store/" + id)
                .then()
                .statusCode(200)
                .body(org.hamcrest.CoreMatchers.containsString("PATCHED_STORE"));
    }

    @Test
    public void testGetStoreByIdSuccess() {
        String body =
                """
                {
                  "name": "STORE_TO_GET",
                  "quantityProductsInStock": 30
                }
                """;

        int id =
                given()
                        .contentType("application/json")
                        .body(body)
                        .when()
                        .post("store")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        given()
                .when()
                .get("store/" + id)
                .then()
                .statusCode(200)
                .body(org.hamcrest.CoreMatchers.containsString("STORE_TO_GET"));
    }


}

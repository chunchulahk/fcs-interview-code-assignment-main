/*
package com.fulfilment.application.monolith.fulfillmentmapping;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FulfilmentEndpointTest {

    private static final String PATH = "/fulfilment/assign";

    @Test
    void testAssignWarehouseSuccess() {

        given()
                .when()
                .post(PATH + "?storeId=1&productId=1&warehouseId=1")
                .then()
                .statusCode(201)
                .body(containsString("\"store\""))
                .body(containsString("\"product\""))
                .body(containsString("\"warehouse\""));
    }

    @Test
    void testStoreMaxWarehousesExceeded() {

        given()
                .when()
                .post(PATH + "?storeId=1&productId=2&warehouseId=3")
                .then()
                .statusCode(500)
                .body(containsString("Store can be fulfilled by max 3 warehouses"));
    }

    @Test
    void testProductMaxWarehousesPerStoreExceeded() {

        given()
                .when()
                .post(PATH + "?storeId=1&productId=1&warehouseId=2")
                .then()
                .statusCode(500)
                .body(containsString("Product can be fulfilled by max 2 warehouses"));
    }

    @Test
    void testWarehouseMaxProductsExceeded() {

        given()
                .when()
                .post(PATH + "?storeId=2&productId=3&warehouseId=1")
                .then()
                .statusCode(500)
                .body(containsString("Warehouse can store max 5 products"));
    }
}
*/

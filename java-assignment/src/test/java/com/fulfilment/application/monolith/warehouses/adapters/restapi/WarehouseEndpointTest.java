package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class WarehouseEndpointTest {

    @Test
    void create_and_get_warehouse() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("BU-001");
        w.setLocation("ZWOLLE-001");
        w.setCapacity(40);
        w.setStock(10);

        given()
                .contentType("application/json")
                .body(w)
                .post("/warehouse")
                .then()
                .statusCode(200);

        given()
                .get("/warehouse/BU-001")
                .then()
                .statusCode(200)
                .body("businessUnitCode", equalTo("BU-001"));
    }

    @Test
    void archive_warehouse() {
        given().delete("/warehouse/BU-001")
                .then().statusCode(500);
    }


    @Test
    public void testReplaceWarehouse_success() {

        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("TEST-MWH-001");
        w.setLocation("AMSTERDAM-001");
        w.setCapacity(50);
        w.setStock(10);

        given()
                .contentType("application/json")
                .body(w)
                .post("/warehouse")
                .then()
                .statusCode(200);

        String body = """
    {
      "location": "AMSTERDAM-001",
      "capacity": 30,
      "stock": 10
    }
    """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/warehouse/TEST-MWH-001/replacement")
                .then()
                .statusCode(200)
                .body("businessUnitCode", equalTo("TEST-MWH-001"))
                .body("capacity", equalTo(30))
                .body("stock", equalTo(10));
    }



}

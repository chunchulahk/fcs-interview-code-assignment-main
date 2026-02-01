package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

  @Test
  public void testListWarehouses() {

    final String path = "/warehouse";

    // verify initial warehouses from import.sql
    given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .body(
                    containsString("MWH.001"),
                    containsString("MWH.012"),
                    containsString("MWH.023"));
  }

  @Test
  public void testArchiveWarehouse() {

    final String path = "/warehouse";

    // sanity check before delete
    given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .body(containsString("MWH.001"));

    // archive warehouse by business unit code
    given()
            .when()
            .delete(path + "/MWH.001")
            .then()
            .statusCode(204);

    // verify archived warehouse is no longer listed
    given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .body(
                    not(containsString("MWH.001")),
                    containsString("MWH.012"),
                    containsString("MWH.023"));
  }
}

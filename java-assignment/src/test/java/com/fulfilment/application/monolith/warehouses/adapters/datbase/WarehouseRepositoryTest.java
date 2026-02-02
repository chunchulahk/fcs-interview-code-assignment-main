package com.fulfilment.application.monolith.warehouses.adapters.datbase;


import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    @Transactional
    void shouldReturnOnlyActiveWarehouseWhenMultipleVersionsExist() {

        // GIVEN: active warehouse
        Warehouse original = new Warehouse();
        original.businessUnitCode = "TEST.BU.001";
        original.location = "ZWOLLE-001";
        original.capacity = 40;
        original.stock = 10;
        original.createdAt = LocalDateTime.now();

        warehouseRepository.create(original);

        // WHEN: warehouse is archived
        original.archivedAt = LocalDateTime.now();
        warehouseRepository.update(original);

        // AND: replacement warehouse created with SAME BU code
        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "TEST.BU.001";
        replacement.location = "ZWOLLE-001";
        replacement.capacity = 35;
        replacement.stock = 10;
        replacement.createdAt = LocalDateTime.now();

        warehouseRepository.create(replacement);

        // THEN: repository must return ONLY active warehouse
        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("TEST.BU.001");

        assertNotNull(result);
        assertNull(result.archivedAt, "Returned warehouse must be active");
        assertEquals(35, result.capacity);
        assertEquals(10, result.stock);
    }
}

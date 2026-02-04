package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {

    static class FakeWarehouseStore implements WarehouseStore {
        boolean createCalled = false;
        List<Warehouse> data = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return data;
        }

        @Override
        public void create(Warehouse warehouse) {
            createCalled = true;
            data.add(warehouse);
        }

        @Override public void update(Warehouse warehouse) {}
        @Override public void remove(Warehouse warehouse) {}

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return data.stream()
                    .filter(w -> buCode.equals(w.businessUnitCode))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Test
    void shouldCreateWarehouseSuccessfully() {
        FakeWarehouseStore store = new FakeWarehouseStore();
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, new LocationGateway());

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU-OK";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 20;
        warehouse.stock = 10;

        useCase.create(warehouse);

        assertTrue(store.createCalled);
        assertNotNull(warehouse.createdAt);
    }

    @Test
    void shouldFailWhenWarehouseIsNull() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new FakeWarehouseStore(), new LocationGateway());

        assertThrows(BusinessRuleViolationException.class,
                () -> useCase.create(null));
    }

    @Test
    void shouldFailWhenLocationCapacityExceeded() {
        FakeWarehouseStore store = new FakeWarehouseStore();
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, new LocationGateway());

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "WH-CAP";
        warehouse.location = "AMSTERDAM-001"; // maxCapacity = 100
        warehouse.capacity = 120;             // exceeds
        warehouse.stock = 10;

        assertThrows(
                BusinessRuleViolationException.class,
                () -> useCase.create(warehouse)
        );
    }

    @Test
    void shouldFailWhenMaxWarehouseCountExceeded() {

        FakeWarehouseStore store = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        // EXISTING warehouse in ZWOLLE-001
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "EXISTING-BU";
        existing.location = "ZWOLLE-001";
        existing.capacity = 20;
        existing.archivedAt = null;
        store.create(existing);

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, locationGateway);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "NEW-BU";
        newWarehouse.location = "ZWOLLE-001";
        newWarehouse.capacity = 10;

        assertThrows(
                BusinessRuleViolationException.class,
                () -> useCase.create(newWarehouse)
        );
    }

}

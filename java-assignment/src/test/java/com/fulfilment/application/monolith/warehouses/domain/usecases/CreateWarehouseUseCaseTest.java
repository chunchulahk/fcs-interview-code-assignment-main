package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

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
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, locationGateway);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU-TEST-001";
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

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(null));
    }

    @Test
    void shouldFailWhenBusinessUnitCodeMissing() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new FakeWarehouseStore(), new LocationGateway());

        Warehouse warehouse = new Warehouse();
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 10;
        warehouse.stock = 5;

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));
    }
}

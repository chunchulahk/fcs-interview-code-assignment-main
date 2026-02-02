package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {

    static class FakeWarehouseStore implements WarehouseStore {
        List<Warehouse> data = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return data;
        }

        @Override
        public void create(Warehouse warehouse) {
            data.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {}

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
    void shouldReplaceWarehouseSuccessfully() {
        FakeWarehouseStore store = new FakeWarehouseStore();

        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU-100";
        current.location = "ZWOLLE-001";
        current.capacity = 20;
        current.stock = 10;
        store.data.add(current);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(store, new LocationGateway());

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU-100";
        replacement.location = "ZWOLLE-001";
        replacement.capacity = 30;
        replacement.stock = 10;

        useCase.replace(replacement);

        assertNotNull(current.archivedAt);
    }

    @Test
    void shouldFailWhenCapacityLessThanStock() {
        FakeWarehouseStore store = new FakeWarehouseStore();

        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU-200";
        current.location = "ZWOLLE-001";
        current.capacity = 20;
        current.stock = 15;
        store.data.add(current);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(store, new LocationGateway());

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU-200";
        replacement.location = "ZWOLLE-001";
        replacement.capacity = 10;
        replacement.stock = 15;

        assertThrows(BusinessRuleViolationException.class,
                () -> useCase.replace(replacement));
    }
}

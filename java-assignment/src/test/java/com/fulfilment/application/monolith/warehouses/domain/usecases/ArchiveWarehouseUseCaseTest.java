package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {

    static class FakeWarehouseStore implements WarehouseStore {
        @Override public List<Warehouse> getAll() { return List.of(); }
        @Override public void create(Warehouse warehouse) {}
        @Override public void update(Warehouse warehouse) {}
        @Override public void remove(Warehouse warehouse) {}
        @Override public Warehouse findByBusinessUnitCode(String buCode) { return null; }
    }

    @Test
    void shouldArchiveWarehouseSuccessfully() {
        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(new FakeWarehouseStore());

        Warehouse warehouse = new Warehouse();
        assertNull(warehouse.archivedAt);

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
    }

    @Test
    void shouldFailWhenAlreadyArchived() {
        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(new FakeWarehouseStore());

        Warehouse warehouse = new Warehouse();
        warehouse.archivedAt = java.time.LocalDateTime.now();

        assertThrows(BusinessRuleViolationException.class,
                () -> useCase.archive(warehouse));
    }
}

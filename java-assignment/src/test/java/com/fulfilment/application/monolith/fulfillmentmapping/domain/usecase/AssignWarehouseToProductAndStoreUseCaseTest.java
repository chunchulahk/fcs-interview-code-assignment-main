package com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.FakeFulfilmentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssignWarehouseToProductAndStoreUseCaseTest {

    @Test
    void shouldAssignSuccessfully() {
        FakeFulfilmentStore store = new FakeFulfilmentStore();
        AssignWarehouseToProductAndStoreUseCase useCase =
                new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store;

        assertDoesNotThrow(() ->
                useCase.assign(1L, 1L, 1L));
    }

    @Test
    void shouldFailWhenStoreHasTooManyWarehouses() {
        FakeFulfilmentStore store = new FakeFulfilmentStore();
        store.addWarehousesToStore(1L, 3);

        AssignWarehouseToProductAndStoreUseCase useCase =
                new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store;

        assertThrows(BusinessRuleViolationException.class,
                () -> useCase.assign(1L, 1L, 4L));
    }

    @Test
    void shouldFailWhenProductHasTooManyWarehousesPerStore() {
        FakeFulfilmentStore store = new FakeFulfilmentStore();
        store.addProductWarehouses(1L, 1L, 2);

        AssignWarehouseToProductAndStoreUseCase useCase =
                new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store;

        assertThrows(BusinessRuleViolationException.class,
                () -> useCase.assign(1L, 1L, 3L));
    }

    AssignWarehouseToProductAndStoreUseCase useCase;
    FakeFulfilmentStore store;

    @BeforeEach
    void setup() {
        store = new FakeFulfilmentStore();
        useCase = new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store; // ✅ REQUIRED
    }


    @Test
    void shouldAssignSuccessfully2() {
        FulfilmentAssignment fa =
                useCase.assign(1L, 1L, 1L);

        assertNotNull(fa);
    }
}

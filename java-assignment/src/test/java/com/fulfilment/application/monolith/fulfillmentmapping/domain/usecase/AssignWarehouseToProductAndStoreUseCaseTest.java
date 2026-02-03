package com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AssignWarehouseToProductAndStoreUseCaseTest {

    static class FakeFulfilmentStore implements FulfilmentStore {

        List<FulfilmentAssignment> data = new ArrayList<>();

        @Override
        public long countWarehousesForStore(Long storeId) {
            return data.stream()
                    .filter(f -> f.store.id.equals(storeId))
                    .map(f -> f.warehouse.id)
                    .distinct()
                    .count();
        }

        @Override
        public long countWarehousesForStoreAndProduct(Long storeId, Long productId) {
            return data.stream()
                    .filter(f -> f.store.id.equals(storeId)
                            && f.product.id.equals(productId))
                    .map(f -> f.warehouse.id)
                    .distinct()
                    .count();
        }

        @Override
        public long countProductsForWarehouse(Long warehouseId) {
            return data.stream()
                    .filter(f -> f.warehouse.id.equals(warehouseId))
                    .map(f -> f.product.id)
                    .distinct()
                    .count();
        }

        @Override
        public FulfilmentAssignment save(Long storeId, Long productId, Long warehouseId) {
            FulfilmentAssignment fa = new FulfilmentAssignment();

            fa.store = new Store();
            fa.store.id = storeId;

            fa.product = new Product();
            fa.product.id = productId;

            fa.warehouse = new DbWarehouse();
            fa.warehouse.id = warehouseId;

            data.add(fa);
            return fa;
        }
    }

    @Test
    void should_assign_successfully() {
        FakeFulfilmentStore store = new FakeFulfilmentStore();
        AssignWarehouseToProductAndStoreUseCase useCase =
                new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store;

        FulfilmentAssignment result =
                useCase.assign(1L, 1L, 1L);

        assertNotNull(result);
        assertEquals(1, store.data.size());
    }

    @Test
    void should_fail_when_store_has_more_than_3_warehouses() {
        FakeFulfilmentStore store = new FakeFulfilmentStore();
        AssignWarehouseToProductAndStoreUseCase useCase =
                new AssignWarehouseToProductAndStoreUseCase();
        useCase.fulfilmentStore = store;

        store.save(1L, 1L, 1L);
        store.save(1L, 2L, 2L);
        store.save(1L, 3L, 3L);

        assertThrows(IllegalStateException.class,
                () -> useCase.assign(1L, 4L, 4L));
    }
}

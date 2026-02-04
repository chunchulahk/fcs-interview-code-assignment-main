package com.fulfilment.application.monolith.fulfillmentmapping.domain;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;

import java.util.HashSet;
import java.util.Set;

public class FakeFulfilmentStore implements FulfilmentStore {

    private final Set<FulfilmentAssignment> data = new HashSet<>();

    // ---------------- COUNTS ----------------

    @Override
    public long countWarehousesForStore(Long storeId) {
        return data.stream()
                .filter(f -> f.store.id.equals(storeId))
                .map(f -> f.warehouse.id)
                .distinct()
                .count();
    }

    @Override
    public long countWarehousesForStoreAndProduct(
            Long storeId, Long productId) {

        return data.stream()
                .filter(f ->
                        f.store.id.equals(storeId)
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

    // ---------------- EXISTS ----------------

    @Override
    public boolean exists(
            Long storeId, Long productId, Long warehouseId) {

        return data.stream().anyMatch(f ->
                f.store.id.equals(storeId)
                        && f.product.id.equals(productId)
                        && f.warehouse.id.equals(warehouseId)
        );
    }

    // ---------------- SAVE ----------------

    @Override
    public FulfilmentAssignment save(
            Long storeId, Long productId, Long warehouseId) {

        if (exists(storeId, productId, warehouseId)) {
            throw new IllegalStateException(
                    "Fulfilment already exists for store, product and warehouse"
            );
        }

        FulfilmentAssignment fa = new FulfilmentAssignment();
        fa.store = TestObjects.store(storeId);
        fa.product = TestObjects.product(productId);
        fa.warehouse = TestObjects.warehouse(warehouseId);

        data.add(fa);
        return fa;
    }

    // ----------------  REQUIRED FIX ----------------

    @Override
    public void deleteByProductId(Long productId) {
        data.removeIf(f -> f.product.id.equals(productId));
    }

    // ---------------- TEST HELPERS ----------------

    public void addWarehousesToStore(Long storeId, int count) {
        for (int i = 0; i < count; i++) {
            save(storeId, (long) i, (long) i);
        }
    }

    public void addProductWarehouses(
            Long storeId, Long productId, int count) {

        for (int i = 0; i < count; i++) {
            save(storeId, productId, (long) i);
        }
    }
}

package com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AssignWarehouseToProductAndStoreUseCase {

    private static final int MAX_WAREHOUSES_PER_STORE = 3;
    private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
    private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

    @Inject
    FulfilmentStore fulfilmentStore;

    @Transactional
    public FulfilmentAssignment assign(
            Long storeId,
            Long productId,
            Long warehouseId) {

        if (fulfilmentStore.exists(storeId, productId, warehouseId)) {
            throw new BusinessRuleViolationException(
                    "Fulfilment already exists for store, product and warehouse"
            );
        }

        if (fulfilmentStore.countWarehousesForStore(storeId)
                >= MAX_WAREHOUSES_PER_STORE) {
            throw new BusinessRuleViolationException(
                    "Store can have max 3 warehouses"
            );
        }

        if (fulfilmentStore.countWarehousesForStoreAndProduct(storeId, productId)
                >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
            throw new BusinessRuleViolationException(
                    "Product can have max 2 warehouses per store"
            );
        }

        if (fulfilmentStore.countProductsForWarehouse(warehouseId)
                >= MAX_PRODUCTS_PER_WAREHOUSE) {
            throw new BusinessRuleViolationException(
                    "Warehouse can store max 5 products"
            );
        }

        return fulfilmentStore.save(storeId, productId, warehouseId);
    }
}

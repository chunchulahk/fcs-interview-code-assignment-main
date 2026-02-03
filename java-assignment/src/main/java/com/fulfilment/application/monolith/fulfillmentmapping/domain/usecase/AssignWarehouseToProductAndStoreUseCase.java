package com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AssignWarehouseToProductAndStoreUseCase {

    @Inject
    FulfilmentStore fulfilmentStore;

    public FulfilmentAssignment assign(Long storeId,
            Long productId,
            Long warehouseId) {

        // Constraint 1: Store → max 3 warehouses
        if (fulfilmentStore.countWarehousesForStore(storeId) >= 3) {
            throw new IllegalStateException("Store can have max 3 warehouses");
        }

        // Constraint 2: Product → max 2 warehouses per store
        if (fulfilmentStore.countWarehousesForStoreAndProduct(storeId, productId) >= 2) {
            throw new IllegalStateException("Product can have max 2 warehouses per store");
        }

        // Constraint 3: Warehouse → max 5 products
        if (fulfilmentStore.countProductsForWarehouse(warehouseId) >= 5) {
            throw new IllegalStateException("Warehouse can store max 5 products");
        }

        // Persist & return created mapping
        return fulfilmentStore.save(storeId, productId, warehouseId);
    }
}

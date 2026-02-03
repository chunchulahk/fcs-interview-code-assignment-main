package com.fulfilment.application.monolith.fulfillmentmapping.domain.ports;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;

public interface FulfilmentStore {

    long countWarehousesForStore(Long storeId);

    long countWarehousesForStoreAndProduct(Long storeId, Long productId);

    long countProductsForWarehouse(Long warehouseId);

    FulfilmentAssignment save(Long storeId, Long productId, Long warehouseId);
}

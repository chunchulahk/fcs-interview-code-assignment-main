package com.fulfilment.application.monolith.fulfillmentmapping.domain;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductResource;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.getEntityManager;

public class FakeFulfilmentStore implements FulfilmentStore {

    private final Set<FulfilmentAssignment> data = new HashSet<>();
    @Inject
    WarehouseStore warehouseStore;

    @Inject
    ProductResource productResource;
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
        DbWarehouse warehouse =
                getEntityManager().find(DbWarehouse.class, warehouseId);
        Product products=getEntityManager().find(Product.class, productId);
        Store store=getEntityManager().find(Store.class, storeId);
        fa.store = TestObjects.store(storeId);
        fa.product = TestObjects.product(productId);
        fa.warehouse = TestObjects.warehouse(warehouseId);


        data.add(fa);
        return fa;
    }
}

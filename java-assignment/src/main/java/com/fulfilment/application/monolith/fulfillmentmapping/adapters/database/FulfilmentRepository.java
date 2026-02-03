package com.fulfilment.application.monolith.fulfillmentmapping.adapters.database;


import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductResource;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfilmentRepository
        implements FulfilmentStore, PanacheRepository<FulfilmentAssignment> {
    @Inject
    WarehouseStore warehouseStore;

    @Inject
    ProductResource productResource;
    @Override
    public long countWarehousesForStore(Long storeId) {
        return count("store.id", storeId);
    }

    @Override
    public long countWarehousesForStoreAndProduct(Long storeId, Long productId) {
        return count("store.id = ?1 and product.id = ?2", storeId, productId);
    }

    @Override
    public long countProductsForWarehouse(Long warehouseId) {
        return count("warehouse.id", warehouseId);
    }

    @Override
    @Transactional
    public FulfilmentAssignment save(Long storeId, Long productId, Long warehouseId) {
        DbWarehouse warehouse =
                getEntityManager().find(DbWarehouse.class, warehouseId);
        Product products=getEntityManager().find(Product.class, productId);
        FulfilmentAssignment fa = new FulfilmentAssignment();
        fa.store = Store.findById(storeId);
        fa.product = products;
        fa.warehouse =warehouse;

        persist(fa);

        return fa;
    }
}

package com.fulfilment.application.monolith.fulfillmentmapping.adapters.database;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.ports.FulfilmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfilmentRepository
        implements FulfilmentStore, PanacheRepository<FulfilmentAssignment> {

    // ---------------- COUNTS ----------------

    @Override
    public long countWarehousesForStore(Long storeId) {
        return getEntityManager()
                .createQuery(
                        "SELECT COUNT(DISTINCT f.warehouse.id) " +
                                "FROM FulfilmentAssignment f " +
                                "WHERE f.store.id = :storeId",
                        Long.class)
                .setParameter("storeId", storeId)
                .getSingleResult();
    }

    @Override
    public long countWarehousesForStoreAndProduct(
            Long storeId, Long productId) {

        return getEntityManager()
                .createQuery(
                        "SELECT COUNT(DISTINCT f.warehouse.id) " +
                                "FROM FulfilmentAssignment f " +
                                "WHERE f.store.id = :storeId " +
                                "AND f.product.id = :productId",
                        Long.class)
                .setParameter("storeId", storeId)
                .setParameter("productId", productId)
                .getSingleResult();
    }

    @Override
    public long countProductsForWarehouse(Long warehouseId) {
        return getEntityManager()
                .createQuery(
                        "SELECT COUNT(DISTINCT f.product.id) " +
                                "FROM FulfilmentAssignment f " +
                                "WHERE f.warehouse.id = :warehouseId",
                        Long.class)
                .setParameter("warehouseId", warehouseId)
                .getSingleResult();
    }

    // ---------------- EXISTS ----------------

    @Override
    public boolean exists(
            Long storeId, Long productId, Long warehouseId) {

        return count(
                "store.id = ?1 and product.id = ?2 and warehouse.id = ?3",
                storeId, productId, warehouseId
        ) > 0;
    }

    // ---------------- SAVE ----------------

    @Override
    @Transactional
    public FulfilmentAssignment save(
            Long storeId, Long productId, Long warehouseId) {

        if (exists(storeId, productId, warehouseId)) {
            throw new IllegalStateException(
                    "Fulfilment already exists for store, product and warehouse"
            );
        }

        FulfilmentAssignment fa = new FulfilmentAssignment();
        fa.store = getEntityManager().find(Store.class, storeId);
        fa.product = getEntityManager().find(Product.class, productId);
        fa.warehouse =
                getEntityManager().find(DbWarehouse.class, warehouseId);

        persist(fa);
        return fa;
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId) {
        getEntityManager()
                .createQuery(
                        "DELETE FROM FulfilmentAssignment f " +
                                "WHERE f.product.id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
    }
}

package com.fulfilment.application.monolith.fulfillmentmapping;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import jakarta.persistence.*;

@Entity
@Table(
        name = "fulfilment_assignment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"store_id", "product_id", "warehouse_id"}
        )
)
public class FulfilmentAssignment {

    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(optional = false)
    public Store store;

    @ManyToOne(optional = false)
    public Product product;

    @ManyToOne(optional = false)
    public DbWarehouse warehouse;
}

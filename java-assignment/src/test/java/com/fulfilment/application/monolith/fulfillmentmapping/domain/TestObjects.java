package com.fulfilment.application.monolith.fulfillmentmapping.domain;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

public class TestObjects {

    public static Store store(Long id) {
        Store s = new Store();
        s.id = id;
        s.name = "STORE-" + id;
        return s;
    }

    public static Product product(Long id) {
        Product p = new Product();
        p.id = id;
        p.name = "PRODUCT-" + id;
        return p;
    }

    public static DbWarehouse warehouse(Long id) {
        DbWarehouse w = new DbWarehouse();
        w.id = id;
        w.businessUnitCode = "WH-" + id;
        return w;
    }
}

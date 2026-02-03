package com.fulfilment.application.monolith.products;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Cacheable
public class Product {

  @Id @GeneratedValue public Long id;

  @Column(length = 40, unique = true)
  public String name;

  @Column(nullable = true)
  public String description;

  @Column(precision = 10, scale = 2, nullable = true)
  public BigDecimal price;

  public int stock;
  @ManyToOne
  public DbWarehouse warehouse;

  public Product() {}

  public Product(String name) {
    this.name = name;
  }
}

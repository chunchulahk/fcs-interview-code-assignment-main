package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationGateway locationGateway;

  @Inject
  public CreateWarehouseUseCase(
          WarehouseStore warehouseStore,
          LocationGateway locationGateway) {
    this.warehouseStore = warehouseStore;
    this.locationGateway = locationGateway;
  }

  @Override
  public void create(Warehouse warehouse) {

    //  Basic validations
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse must not be null");
    }

    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new IllegalArgumentException("Business Unit Code is required");
    }

    //  Business Unit Code uniqueness
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalStateException("Business Unit Code already exists");
    }

    //  Location validation
    Location location = locationGateway.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new IllegalArgumentException("Invalid warehouse location");
    }

    //  Fetch existing ACTIVE warehouses for this location
    List<Warehouse> existingWarehouses =
            warehouseStore.getAll().stream()
                    .filter(w ->
                            w.location.equals(warehouse.location)
                                    && w.archivedAt == null)
                    .toList();

    //  Max number of warehouses per location
    if (existingWarehouses.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalStateException(
              "Maximum number of warehouses reached for location " + location.identification);
    }

    //  Aggregate capacity validation (location-level)
    int usedCapacity =
            existingWarehouses.stream()
                    .mapToInt(w -> w.capacity)
                    .sum();

    if (usedCapacity + warehouse.capacity > location.maxCapacity) {
      throw new IllegalStateException(
              "Total warehouse capacity exceeds location limit");
    }

    //  Stock vs capacity (warehouse-level)
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Stock exceeds warehouse capacity");
    }

    // Set creation timestamp
    warehouse.createdAt = LocalDateTime.now();

    //  Persist warehouse
    warehouseStore.create(warehouse);
  }
}

package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.common.exceptions.DuplicateResourceException;
import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

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

    if (warehouse == null) {
      throw new BusinessRuleViolationException("Warehouse must not be null");
    }

    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new BusinessRuleViolationException("Business Unit Code is required");
    }

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new DuplicateResourceException("Business Unit Code already exists");
    }

    Location location =
            locationGateway.resolveByIdentifier(warehouse.location);

        /* =====================================================
           FIX #1: Max warehouses per location
           ===================================================== */
    long activeWarehousesInLocation =
            warehouseStore.getAll().stream()
                    .filter(w -> w.archivedAt == null)
                    .filter(w -> w.location.equals(location.identification))
                    .count();

    if (activeWarehousesInLocation >= location.maxNumberOfWarehouses) {
      throw new BusinessRuleViolationException(
              "Maximum number of warehouses reached for this location");
    }

        /* =====================================================
           FIX #2: Total capacity per location
           ===================================================== */
    int usedCapacity =
            warehouseStore.getAll().stream()
                    .filter(w -> w.archivedAt == null)
                    .filter(w -> w.location.equals(location.identification))
                    .mapToInt(w -> w.capacity)
                    .sum();

    if (usedCapacity + warehouse.capacity > location.maxCapacity) {
      throw new BusinessRuleViolationException(
              "Total warehouse capacity exceeds location limit");
    }

    if (warehouse.stock != null && warehouse.stock > warehouse.capacity) {
      throw new BusinessRuleViolationException("Stock exceeds capacity");
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(warehouse);
  }
}

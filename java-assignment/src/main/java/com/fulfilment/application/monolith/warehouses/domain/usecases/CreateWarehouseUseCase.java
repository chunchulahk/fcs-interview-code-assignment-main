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
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOGGER =
          Logger.getLogger(CreateWarehouseUseCase.class);

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

    Location location = locationGateway.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new BusinessRuleViolationException("Invalid warehouse location");
    }

    if (warehouse.capacity == null || warehouse.capacity > location.maxCapacity) {
      throw new BusinessRuleViolationException("Capacity exceeds location limit");
    }

    if (warehouse.stock == null || warehouse.stock > warehouse.capacity) {
      throw new BusinessRuleViolationException("Stock exceeds capacity");
    }

    warehouse.createdAt = LocalDateTime.now();

    LOGGER.info("Creating warehouse: " + warehouse.businessUnitCode);

    warehouseStore.create(warehouse);
  }
}

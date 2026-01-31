package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private static final Logger LOGGER =
          Logger.getLogger(ReplaceWarehouseUseCase.class);
  private final WarehouseStore warehouseStore;
  private final LocationGateway locationGateway;

  @Inject
  public ReplaceWarehouseUseCase(
          WarehouseStore warehouseStore,
          LocationGateway locationGateway) {
    this.warehouseStore = warehouseStore;
    this.locationGateway = locationGateway;
  }

  @Override
  public void replace(Warehouse newWarehouse) {

    if (newWarehouse == null) {
      throw new IllegalArgumentException("Warehouse must not be null");
    }

    //  Load current active warehouse
    Warehouse current =
            warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

    if (current == null || current.archivedAt != null) {
      throw new IllegalStateException("Active warehouse not found");
    }

    //  Location validation
    Location location =
            locationGateway.resolveByIdentifier(newWarehouse.location);

    if (location == null) {
      throw new IllegalArgumentException("Invalid warehouse location");
    }

    //  Capacity must handle existing stock
    if (newWarehouse.capacity < current.stock) {
      throw new IllegalArgumentException(
              "New capacity cannot be less than existing stock");
    }

    //  Stock must match
    if (!newWarehouse.stock.equals(current.stock)) {
      throw new IllegalArgumentException(
              "Replacement warehouse stock must match existing stock");
    }

    //  Fetch other ACTIVE warehouses at the same location (excluding current)
    List<Warehouse> existingWarehouses =
            warehouseStore.getAll().stream()
                    .filter(w ->
                            w.archivedAt == null
                                    && w.location.equals(newWarehouse.location)
                                    && !w.businessUnitCode.equals(current.businessUnitCode))
                    .toList();

    //  Aggregate capacity validation
    int usedCapacity =
            existingWarehouses.stream()
                    .mapToInt(w -> w.capacity)
                    .sum();

    if (usedCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalStateException(
              "Total warehouse capacity exceeds location limit");
    }

    //  Archive old warehouse
    current.archivedAt = LocalDateTime.now();
    warehouseStore.update(current);
    LOGGER.info("update warehouse: " + current.businessUnitCode);
    //  Create replacement warehouse
    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);
    LOGGER.info("new warehouse created: " + newWarehouse.businessUnitCode);
  }
}

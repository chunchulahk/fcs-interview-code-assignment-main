package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.common.exceptions.BusinessRuleViolationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private static final Logger LOGGER =
          Logger.getLogger(ArchiveWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {

    if (warehouse == null) {
      throw new BusinessRuleViolationException("Warehouse must not be null");
    }

    if (warehouse.archivedAt != null) {
      throw new BusinessRuleViolationException("Warehouse already archived");
    }

    LOGGER.info("Archiving warehouse: " + warehouse.businessUnitCode);

    warehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(warehouse);
  }
}

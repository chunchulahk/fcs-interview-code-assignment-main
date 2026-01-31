package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import org.jboss.logging.Logger;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER =
          Logger.getLogger(WarehouseResourceImpl.class);

  @Inject WarehouseRepository warehouseRepository;
  @Inject LocationGateway locationGateway;

  @Inject CreateWarehouseUseCase createWarehouseUseCase;
  @Inject ArchiveWarehouseUseCase archiveWarehouseUseCase;
  @Inject ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    LOGGER.info("Fetching all warehouse units");
    return warehouseRepository.getAll().stream()
            .map(this::toWarehouseResponse)
            .toList();
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(
          @NotNull com.warehouse.api.beans.Warehouse data) {

    LOGGER.infof(
            "Create warehouse request received. BU=%s, Location=%s",
            data.getBusinessUnitCode(), data.getLocation());

    if (warehouseRepository.findByBusinessUnitCode(data.getBusinessUnitCode()) != null) {
      LOGGER.warn("Business Unit Code already exists: " + data.getBusinessUnitCode());
      throw new WebApplicationException("Business Unit Code already exists.", 409);
    }

    Location location = locationGateway.resolveByIdentifier(data.getLocation());
    if (location == null) {
      LOGGER.warn("Invalid location: " + data.getLocation());
      throw new WebApplicationException("Invalid Location.", 422);
    }

    if (data.getCapacity() > location.maxCapacity) {
      throw new WebApplicationException("Capacity exceeds location limit.", 422);
    }

    if (data.getStock() > data.getCapacity()) {
      throw new WebApplicationException("Stock exceeds capacity.", 422);
    }

    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    warehouse.createdAt = LocalDateTime.now();

    createWarehouseUseCase.create(warehouse);

    LOGGER.info("Warehouse created successfully: " + warehouse.businessUnitCode);

    return toWarehouseResponse(warehouse);
  }

  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
    LOGGER.info("Fetching warehouse by BU code: " + id);

    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) {
      LOGGER.warn("Warehouse not found: " + id);
      throw new WebApplicationException("Warehouse not found.", 404);
    }
    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    LOGGER.info("Archive request received for warehouse: " + id);

    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) {
      LOGGER.warn("Warehouse not found for archive: " + id);
      throw new WebApplicationException("Warehouse not found.", 404);
    }

    archiveWarehouseUseCase.archive(warehouse);

    LOGGER.info("Warehouse archived successfully: " + id);
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
          String businessUnitCode,
          @NotNull com.warehouse.api.beans.Warehouse data) {

    LOGGER.info("Replace request received for warehouse: " + businessUnitCode);

    Warehouse current = warehouseRepository.findByBusinessUnitCode(businessUnitCode);
    if (current == null || current.archivedAt != null) {
      LOGGER.warn("Invalid warehouse for replace: " + businessUnitCode);
      throw new WebApplicationException("Invalid warehouse.", 409);
    }

    current.archivedAt = LocalDateTime.now();
    warehouseRepository.update(current);

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = businessUnitCode;
    replacement.location = data.getLocation();
    replacement.capacity = data.getCapacity();
    replacement.stock = data.getStock();
    replacement.createdAt = LocalDateTime.now();

    replaceWarehouseUseCase.replace(replacement);

    LOGGER.info("Warehouse replaced successfully: " + businessUnitCode);

    return toWarehouseResponse(replacement);
  }

  private com.warehouse.api.beans.Warehouse toWarehouseResponse(Warehouse warehouse) {
    var response = new com.warehouse.api.beans.Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }
}

package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.*;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/warehouse")   // 🔴 REQUIRED
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject CreateWarehouseUseCase createWarehouseUseCase;
    @Inject ArchiveWarehouseUseCase archiveWarehouseUseCase;
    @Inject ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Inject
    WarehouseRepository warehouseRepository;

    @Override
    public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(
            com.warehouse.api.beans.Warehouse data) {

        Warehouse domain = toDomain(data);
        createWarehouseUseCase.create(domain);
        return toResponse(domain);
    }

    @Override
    public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {
        Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new WebApplicationException("Warehouse not found", 404);
        }
        return toResponse(warehouse);
    }

    @Override
    @Transactional
    public void archiveAWarehouseUnitByID(String id) {
        Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        archiveWarehouseUseCase.archive(warehouse);
    }

    /*@Override
    @Transactional
    public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode,
            com.warehouse.api.beans.Warehouse data) {

        Warehouse replacement = toDomain(data);
        replacement.businessUnitCode = businessUnitCode;
        replaceWarehouseUseCase.replace(replacement);
        return toResponse(replacement);
    }*/

    @Override
    @Transactional
    public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode,
            com.warehouse.api.beans.Warehouse data) {

        Warehouse replacement = toDomain(data);
        replacement.businessUnitCode = businessUnitCode;
        replaceWarehouseUseCase.replace(replacement);
        Warehouse persisted =
                warehouseRepository.findByBusinessUnitCode(businessUnitCode);
        return toResponse(persisted);
    }


    private Warehouse toDomain(com.warehouse.api.beans.Warehouse dto) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = dto.getBusinessUnitCode();
        w.location = dto.getLocation();
        w.capacity = dto.getCapacity();
        w.stock = dto.getStock();
        return w;
    }

    private com.warehouse.api.beans.Warehouse toResponse(Warehouse w) {
        var r = new com.warehouse.api.beans.Warehouse();
        r.setBusinessUnitCode(w.businessUnitCode);
        r.setLocation(w.location);
        r.setCapacity(w.capacity);
        r.setStock(w.stock);
        return r;
    }
}

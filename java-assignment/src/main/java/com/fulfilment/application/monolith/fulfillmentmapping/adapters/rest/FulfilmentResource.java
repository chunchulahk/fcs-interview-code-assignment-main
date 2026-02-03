package com.fulfilment.application.monolith.fulfillmentmapping.adapters.rest;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase.AssignWarehouseToProductAndStoreUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/fulfilment")
@Consumes("application/json")
@Produces("application/json")
public class FulfilmentResource {

    @Inject
    AssignWarehouseToProductAndStoreUseCase useCase;

    @POST
    public Response assign(
            @QueryParam("storeId") Long storeId,
            @QueryParam("productId") Long productId,
            @QueryParam("warehouseId") Long warehouseId) {

        FulfilmentAssignment result =
                useCase.assign(storeId, productId, warehouseId);

        return Response.status(201).entity(result).build();
    }
}

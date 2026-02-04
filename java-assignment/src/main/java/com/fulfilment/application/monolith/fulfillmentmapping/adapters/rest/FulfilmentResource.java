package com.fulfilment.application.monolith.fulfillmentmapping.adapters.rest;

import com.fulfilment.application.monolith.fulfillmentmapping.FulfilmentAssignment;
import com.fulfilment.application.monolith.fulfillmentmapping.domain.usecase.AssignWarehouseToProductAndStoreUseCase;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/fulfilment")
@Produces(MediaType.APPLICATION_JSON)
public class FulfilmentResource {

    @Inject
    AssignWarehouseToProductAndStoreUseCase useCase;

    @POST
    @Path("/assign")
    public Response assign(
            @QueryParam("storeId") Long storeId,
            @QueryParam("productId") Long productId,
            @QueryParam("warehouseId") Long warehouseId) {

        FulfilmentAssignment result =
                useCase.assign(storeId, productId, warehouseId);

        return Response
                .status(Response.Status.CREATED)
                .entity(result)
                .build();
    }
}

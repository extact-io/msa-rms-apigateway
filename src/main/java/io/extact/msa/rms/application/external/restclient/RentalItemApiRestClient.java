package io.extact.msa.rms.application.external.restclient;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.application.external.dto.AddRentalItemDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.client.PropagateLoginClientHeadersFactory;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api-item")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterClientHeaders(PropagateLoginClientHeadersFactory.class)
@Path("/items")
public interface RentalItemApiRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<RentalItemDto> getAll();

    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    RentalItemDto get(@PathParam("itemId") Integer itemId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    RentalItemDto add(AddRentalItemDto dto);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    RentalItemDto update(RentalItemDto dto);

    @DELETE
    @Path("/{itemId}")
    void delete(@PathParam("itemId") Integer itemId);
}

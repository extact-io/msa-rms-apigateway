package io.extact.msa.rms.application.external.restclient;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.application.external.dto.AddRentalItemDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.login.PropagateLoginClientHeadersFactory;
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

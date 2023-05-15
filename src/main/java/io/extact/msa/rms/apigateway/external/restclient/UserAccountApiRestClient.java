package io.extact.msa.rms.apigateway.external.restclient;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.apigateway.external.dto.AddUserAccountDto;
import io.extact.msa.rms.apigateway.external.dto.UserAccountDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.interceptor.NetworkConnectionErrorAware;
import io.extact.msa.rms.platform.fw.external.PropagateLoginUserClientHeadersFactory;
import io.extact.msa.rms.platform.fw.external.PropagateResponseExceptionMapper;

@RegisterRestClient(configKey = "web-api-user")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserClientHeadersFactory.class)
@Path("api/users")
@NetworkConnectionErrorAware
public interface UserAccountApiRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserAccountDto> getAll();

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountDto get(@PathParam("userId") Integer userId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountDto add(AddUserAccountDto dto) throws BusinessFlowException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountDto update(UserAccountDto dto);

    @DELETE
    @Path("/{userId}")
    void delete(@PathParam("userId") Integer userId) throws BusinessFlowException;

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    UserAccountDto authenticate(@QueryParam("loginId") String loginId, @QueryParam("password") String password);
}

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.extact.msa.rms.application.external.dto.AddUserAccountDto;
import io.extact.msa.rms.application.external.dto.UserAccountDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.login.PropagateLoginClientHeadersFactory;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api-user")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterClientHeaders(PropagateLoginClientHeadersFactory.class)
@Path("/users")
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

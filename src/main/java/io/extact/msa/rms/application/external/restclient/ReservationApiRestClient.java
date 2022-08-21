package io.extact.msa.rms.application.external.restclient;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import io.extact.msa.rms.application.external.dto.AddReservationDto;
import io.extact.msa.rms.application.external.dto.ReservationDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.client.PropagateLoginClientHeadersFactory;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.webapi.client.ExceptionPropagateClientMapper;

@RegisterRestClient(configKey = "web-api-reservation")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(ExceptionPropagateClientMapper.class)
@RegisterClientHeaders(PropagateLoginClientHeadersFactory.class)
@Path("/reservations")
public interface ReservationApiRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationDto> getAll();

    @GET
    @Path("/item/{itemId}/startdate/{startDate}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationDto> findByRentalItemAndStartDate(@PathParam("itemId") Integer itemId,
            @PathParam("startDate") LocalDate startDate);

    @GET
    @Path("/reserver/{reserverId}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationDto> findByReserverId(@PathParam("reserverId") Integer reserverId);

    @GET
    @Path("/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationDto> findByRentalItemId(@PathParam("itemId") Integer itemId);

    @GET
    @Path("/item/overlapped")
    @Produces(MediaType.APPLICATION_JSON)
    List<ReservationDto> findOverlappedReservations(@QueryParam("from") LocalDateTime from,
            @QueryParam("to") LocalDateTime to);

    @GET
    @Path("/item/{itemId}/overlapped")
    @Produces(MediaType.APPLICATION_JSON)
    ReservationDto findOverlappedReservation(@PathParam("itemId") Integer itemId,
            @QueryParam("from") LocalDateTime from,
            @QueryParam("to") LocalDateTime to);

    @GET
    @Path("/has-item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    boolean hasRentalItemWith(@PathParam("itemId") Integer itemId);

    @GET
    @Path("/has-user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    boolean hasUserAccountWith(@PathParam("userId") Integer userId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReservationDto add(AddReservationDto dto) throws BusinessFlowException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ReservationDto update(ReservationDto dto);

    @DELETE
    @Path("/{reservationId}")
    void delete(@PathParam("reservationId") Integer reservationId) throws BusinessFlowException;

    @DELETE
    @Path("cancel")
    void cancel(@QueryParam("reservationId") Integer reservationId, @QueryParam("reserverId") Integer reserverId)
            throws BusinessFlowException;
}

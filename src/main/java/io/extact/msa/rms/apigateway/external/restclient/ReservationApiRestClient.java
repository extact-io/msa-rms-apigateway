package io.extact.msa.rms.apigateway.external.restclient;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import io.extact.msa.rms.apigateway.external.dto.AddReservationDto;
import io.extact.msa.rms.apigateway.external.dto.ReservationDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.external.PropagateLoginUserClientHeadersFactory;
import io.extact.msa.rms.platform.fw.external.PropagateResponseExceptionMapper;

@RegisterRestClient(configKey = "web-api-reservation")
@RegisterProvider(RmsTypeParameterFeature.class)
@RegisterProvider(PropagateResponseExceptionMapper.class)
@RegisterClientHeaders(PropagateLoginUserClientHeadersFactory.class)
@Path("api/reservations")
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

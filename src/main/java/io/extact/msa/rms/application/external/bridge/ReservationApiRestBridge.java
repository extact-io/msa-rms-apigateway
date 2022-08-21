package io.extact.msa.rms.application.external.bridge;

import static io.extact.msa.rms.application.external.ApiType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.application.external.ReservationApi;
import io.extact.msa.rms.application.external.dto.AddReservationDto;
import io.extact.msa.rms.application.external.dto.ReservationDto;
import io.extact.msa.rms.application.external.restclient.ReservationApiRestClient;
import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
public class ReservationApiRestBridge implements ReservationApi {

    private ReservationApiRestClient client;

    @Inject
    public ReservationApiRestBridge(@RestClient ReservationApiRestClient client) {
        this.client = client;
    }

    @Override
    public List<ReservationDto> getAll() {
        return client.getAll();
    }

    @Override
    public List<ReservationDto> findByRentalItemAndStartDate(int itemId, LocalDate startDate) {
        return client.findByRentalItemAndStartDate(itemId, startDate);
    }

    @Override
    public List<ReservationDto> findByReserverId(int reserverId) {
        return client.findByReserverId(reserverId);
    }

    @Override
    public List<ReservationDto> findByRentalItemId(int itemId) {
        return client.findByRentalItemId(itemId);
    }

    @Override
    public List<ReservationDto> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        return client.findOverlappedReservations(from, to);
    }

    @Override
    public Optional<ReservationDto> findOverlappedReservation(int itemId, LocalDateTime from, LocalDateTime to) {
        return Optional.ofNullable(client.findOverlappedReservation(itemId, from, to));
    }

    @Override
    public boolean hasRentalItemWith(int itemId) {
        return client.hasRentalItemWith(itemId);
    }

    @Override
    public boolean hasUserAccountWith(int userId) {
        return client.hasUserAccountWith(userId);
    }

    @Override
    public ReservationDto add(AddReservationDto dto) throws BusinessFlowException {
        return client.add(dto);
    }

    @Override
    public ReservationDto update(ReservationDto dto) {
        return client.update(dto);
    }

    @Override
    public void delete(int reservationId) throws BusinessFlowException {
        client.delete(reservationId);
    }

    @Override
    public void cancel(int reservationId, int reserverId) throws BusinessFlowException {
        client.cancel(reservationId, reserverId);
    }
}

package io.extact.msa.rms.application.external;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.QueryParam;

import io.extact.msa.rms.application.external.dto.AddReservationDto;
import io.extact.msa.rms.application.external.dto.ReservationDto;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

public interface ReservationApi {

    List<ReservationDto> getAll();

    List<ReservationDto> findByRentalItemAndStartDate(int itemId, LocalDate startDate);

    List<ReservationDto> findByReserverId(int reserverId);

    List<ReservationDto> findByRentalItemId(int itemId);

    List<ReservationDto> findOverlappedReservations(LocalDateTime from, LocalDateTime to);

    Optional<ReservationDto> findOverlappedReservation(int itemId, LocalDateTime from, LocalDateTime to);

    boolean hasRentalItemWith(int itemId);

    boolean hasUserAccountWith(int userId);

    ReservationDto add(AddReservationDto dto) throws BusinessFlowException;

    ReservationDto update(ReservationDto dto);

    void delete(int reservationId) throws BusinessFlowException;

    void cancel(int reservationId, @QueryParam("reserverId") int reserverId) throws BusinessFlowException;
}

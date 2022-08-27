package io.extact.msa.rms.application.external.stub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.application.external.dto.AddReservationDto;
import io.extact.msa.rms.application.external.dto.ReservationDto;
import io.extact.msa.rms.application.external.restclient.ReservationApiRestClient;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.test.stub.ReservationMemoryStub;
import io.extact.msa.rms.platform.test.stub.dto.AddReservationStubDto;
import io.extact.msa.rms.platform.test.stub.dto.ReservationStubDto;

@ApplicationScoped
@Path("/reservations")
public class ReservationApiRemoteStub implements ReservationApiRestClient {

    private ReservationMemoryStub stub = new ReservationMemoryStub();

    @PostConstruct
    public void init() {
        stub.init();
    }

    @Override
    public List<ReservationDto> getAll() {
        return stub.getAll().stream()
                .map(this::convertReservationDto)
                .toList();
    }

    @Override
    public List<ReservationDto> findByRentalItemAndStartDate(Integer itemId, LocalDate startDate) {
        return stub.findByRentalItemAndStartDate(itemId, startDate).stream()
                .map(this::convertReservationDto)
                .toList();
    }

    @Override
    public List<ReservationDto> findByReserverId(Integer reserverId) {
        return stub.findByReserverId(reserverId).stream()
                .map(this::convertReservationDto)
                .toList();
    }

    @Override
    public List<ReservationDto> findByRentalItemId(Integer itemId) {
        return stub.findByRentalItemId(itemId).stream()
                .map(this::convertReservationDto)
                .toList();
    }

    @Override
    public List<ReservationDto> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        return stub.findOverlappedReservations(from, to).stream()
                .map(this::convertReservationDto)
                .toList();
    }

    @Override
    public ReservationDto findOverlappedReservation(Integer itemId, LocalDateTime from, LocalDateTime to) {
        return stub.findOverlappedReservation(itemId, from, to)
                .map(this::convertReservationDto)
                .orElse(null);
    }

    @Override
    public boolean hasRentalItemWith(Integer itemId) {
        return stub.hasRentalItemWith(itemId);
    }

    @Override
    public boolean hasUserAccountWith(Integer userId) {
        return stub.hasUserAccountWith(userId);
    }

    @Override
    public ReservationDto add(AddReservationDto dto) throws BusinessFlowException {
        return stub.add(convertAddReservationDto(dto))
                .transform(this::convertReservationDto);
    }

    @Override
    public ReservationDto update(ReservationDto dto) {
        return stub.update(convertReservationStubDto(dto))
                .transform(this::convertReservationDto);
    }

    @Override
    public void delete(Integer reservationId) throws BusinessFlowException {
        stub.delete(reservationId);
    }

    @Override
    public void cancel(Integer reservationId, Integer reserverId) throws BusinessFlowException {
        stub.cancel(reservationId, reserverId);
    }

    // ----------------------------------------------------- convert methods

    private ReservationDto convertReservationDto(ReservationStubDto src) {
        return ReservationDto.of(src.getId(), src.getStartDateTime(), src.getEndDateTime(), src.getNote(),
                src.getRentalItemId(), src.getUserAccountId());
    }

    private ReservationStubDto convertReservationStubDto(ReservationDto src) {
        return ReservationStubDto.of(src.getId(), src.getStartDateTime(), src.getEndDateTime(), src.getNote(),
                src.getRentalItemId(), src.getUserAccountId(), null, null);
    }

    private AddReservationStubDto convertAddReservationDto(AddReservationDto src) {
        return AddReservationStubDto.of(src.getStartDateTime(), src.getEndDateTime(), src.getNote(),
                src.getRentalItemId(), src.getUserAccountId());
    }
}

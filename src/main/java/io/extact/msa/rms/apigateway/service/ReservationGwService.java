package io.extact.msa.rms.apigateway.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import io.extact.msa.rms.apigateway.external.RentalItemApi;
import io.extact.msa.rms.apigateway.external.ReservationApi;
import io.extact.msa.rms.apigateway.external.UserAccountApi;
import io.extact.msa.rms.apigateway.external.dto.AddReservationDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.ReservationDto;
import io.extact.msa.rms.apigateway.external.dto.UserAccountDto;
import io.extact.msa.rms.apigateway.model.RentalItemModel;
import io.extact.msa.rms.apigateway.model.ReservationModel;
import io.extact.msa.rms.apigateway.service.event.DeleteRentalItemEvent;
import io.extact.msa.rms.apigateway.service.event.DeleteUserAccountEvent;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.login.LoginUserUtils;
import io.opentracing.Span;
import io.opentracing.Tracer;

@ApplicationScoped
public class ReservationGwService {

    @Inject
    private Tracer tracer;

    private ReservationApi reservationApi;
    private RentalItemApi itemApi;
    private UserAccountApi userApi;

    @Inject
    public ReservationGwService(ReservationApi reservationApi, RentalItemApi itemApi, UserAccountApi userApi) {
        this.reservationApi = reservationApi;
        this.itemApi = itemApi;
        this.userApi = userApi;
    }

    public List<ReservationModel> getAll() {
        return reservationApi.getAll().stream()
                .map(this::composeModel)
                .toList();
    }

    public ReservationModel add(ReservationModel addModel) throws BusinessFlowException {
        return reservationApi
                .add(addModel.transform(AddReservationDto::from))
                .toModel(this::composeModel);
    }

    public ReservationModel update(ReservationModel updateModel) {
        return reservationApi
                .update(updateModel.transform(ReservationDto::from))
                .toModel(this::composeModel);
    }

    public void delete(int deleteId) throws BusinessFlowException {
        reservationApi.delete(deleteId);
    }

    public List<ReservationModel> findByRentalItemAndStartDate(int itemId, LocalDate startDate) {
        return reservationApi.findByRentalItemAndStartDate(itemId, startDate).stream()
                .map(this::composeModel)
                .toList();
    }

    public List<ReservationModel> findByReserverId(int reserverId) {
        return reservationApi.findByReserverId(reserverId).stream()
                .map(this::composeModel)
                .toList();
    }

    public List<ReservationModel> findByRentalItemId(int itemId) {
        return reservationApi.findByRentalItemId(itemId).stream()
                .map(this::composeModel)
                .toList();
    }

    public List<RentalItemModel> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to) {
        var itemIds = reservationApi.findOverlappedReservations(from, to).stream()
                .map(ReservationDto::getRentalItemId)
                .toList();
        return itemApi.getAll().stream()
                .filter(item -> !itemIds.contains(item.getId()))
                .map(RentalItemDto::toModel)
                .toList();
    }

    public boolean canRentedItemAtTerm(int itemId, LocalDateTime from, LocalDateTime to) {
        return reservationApi.findOverlappedReservation(itemId, from, to).isEmpty();
    }

    public void cancel(int reservationId) throws BusinessFlowException {
        reservationApi.cancel(reservationId, LoginUserUtils.get().getUserId());
    }

    // ----------------------------------------------------------------- Observer methods for CDI

    void onBeforeDeleteRentalItem(@Observes DeleteRentalItemEvent event) { // called sync
        if (reservationApi.hasRentalItemWith(event.deleteId())) {
            throw new BusinessFlowException("Cannot be deleted because it is referenced in the reservation.",
                    CauseType.REFERED);
        }
    }

    void onBeforeDeleteUserAccount(@Observes DeleteUserAccountEvent event) { // called sync
        if (reservationApi.hasUserAccountWith(event.deleteId())) {
            throw new BusinessFlowException("Cannot be deleted because it is referenced in the reservation.",
                    CauseType.REFERED);
        }
    }

    // ----------------------------------------------------------------- private methods

    private ReservationModel composeModel(ReservationDto reservationDto) {
        CompletableFuture<RentalItemDto> itemFuture = new SpanContextAware(tracer)
                .supplyAsync(() -> itemApi.get(reservationDto.getRentalItemId()));
        CompletableFuture<UserAccountDto> userFuture = new SpanContextAware(tracer)
                .supplyAsync(() -> userApi.get(reservationDto.getUserAccountId()));
        CompletableFuture.allOf(itemFuture, userFuture).join();

        var itemModel = Optional.ofNullable(itemFuture.join())
                .orElseThrow(() -> new BusinessFlowException(
                        "target does not exist for id:[" + reservationDto.getRentalItemId() + "]",
                        CauseType.NOT_FOUND))
                .toModel();
        var userModel = Optional.ofNullable(userFuture.join())
                .orElseThrow(() -> new BusinessFlowException(
                        "target does not exist for id:[" + reservationDto.getUserAccountId() + "]",
                        CauseType.NOT_FOUND))
                .toModel();

        var reservationModel = reservationDto.toModel();
        reservationModel.setRentalItem(itemModel);
        reservationModel.setUserAccount(userModel);

        return reservationModel;
    }

    // ----------------------------------------------------------------- inner class

    static class SpanContextAware {
        private Tracer tracer;
        private Span currentSpan;
        SpanContextAware(Tracer tracer) {
            this.tracer = tracer;
            this.currentSpan = tracer.activeSpan();
        }
        <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
            return CompletableFuture.supplyAsync(() -> {
                tracer.scopeManager().activate(currentSpan);
                return supplier.get();
            });
        }
    }
}

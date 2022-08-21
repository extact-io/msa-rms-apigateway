package io.extact.msa.rms.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.extact.msa.rms.application.external.RentalItemApi;
import io.extact.msa.rms.application.external.ReservationApi;
import io.extact.msa.rms.application.external.UserAccountApi;
import io.extact.msa.rms.application.external.dto.AddReservationDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;
import io.extact.msa.rms.application.external.dto.ReservationDto;
import io.extact.msa.rms.application.model.RentalItemModel;
import io.extact.msa.rms.application.model.ReservationModel;
import io.extact.msa.rms.application.service.event.DeleteRentalItemEvent;
import io.extact.msa.rms.application.service.event.DeleteUserAccountEvent;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserUtils;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;

@ApplicationScoped
public class ReservationAppService {

    private ReservationApi reservationApi;
    private RentalItemApi itemApi;
    private UserAccountApi userApi;

    @Inject
    public ReservationAppService(ReservationApi reservationApi, RentalItemApi itemApi, UserAccountApi userApi) {
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
        var itemModel = itemApi.get(reservationDto.getRentalItemId())
                .orElseThrow(() -> new BusinessFlowException(
                        "target does not exist for id:[" + reservationDto.getRentalItemId() + "]",
                        CauseType.NOT_FOUND))
                .toModel();
        var userModel = userApi.get(reservationDto.getUserAccountId())
                .orElseThrow(() -> new BusinessFlowException(
                        "target does not exist for id:[" + reservationDto.getUserAccountId() + "]",
                        CauseType.NOT_FOUND))
                .toModel();
        var reservationModel = reservationDto.toModel();
        reservationModel.setRentalItem(itemModel);
        reservationModel.setUserAccount(userModel);
        return reservationModel;
    }
}

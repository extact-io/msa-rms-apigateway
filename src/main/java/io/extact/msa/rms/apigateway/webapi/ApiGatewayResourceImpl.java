package io.extact.msa.rms.apigateway.webapi;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import io.extact.msa.rms.apigateway.service.RentalItemGwService;
import io.extact.msa.rms.apigateway.service.ReservationGwService;
import io.extact.msa.rms.apigateway.service.UserAccountGwService;
import io.extact.msa.rms.apigateway.webapi.dto.AddRentalItemEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddUserAccountEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.RentalItemResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.core.validate.ValidateGroup;
import io.extact.msa.rms.platform.core.validate.ValidateParam;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.fw.exception.interceptor.ExceptionUnwrapAware;
import io.extact.msa.rms.platform.fw.login.LoginUserUtils;

@Path("rms")
@SecurityRequirement(name = "RmsJwtAuth")
@ApplicationScoped
@ValidateParam
@ExceptionUnwrapAware
public class ApiGatewayResourceImpl implements ApiGatewayResource {

    private ReservationGwService reservationService;
    private RentalItemGwService itemService;
    private UserAccountGwService userService;

    @Inject
    public ApiGatewayResourceImpl(ReservationGwService reservationService, RentalItemGwService itemService,
            UserAccountGwService userService) {
        this.reservationService = reservationService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public List<ReservationResourceDto> findReservationByRentalItemAndStartDate(Integer itemId, LocalDate date) {
        return reservationService.findByRentalItemAndStartDate(itemId, date)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> findReservationByReserverId(Integer reserverId) {
        return reservationService.findByReserverId(reserverId)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<ReservationResourceDto> getOwnReservations() {
        return findReservationByReserverId(LoginUserUtils.get().getUserId());
    }

    @Override
    public List<RentalItemResourceDto> getAllRentalItems() {
        return itemService.getAll()
                .stream()
                .map(RentalItemResourceDto::from)
                .toList();
    }

    @ValidateGroup(groups = Add.class)
    @Override
    public ReservationResourceDto addReservation(AddReservationEventDto addDto) {
        return reservationService.add(addDto.toModel())
                .transform(ReservationResourceDto::from);
    }

    @Override
    public void cancelReservation(Integer reservationId) {
        reservationService.cancel(reservationId);
    }

    @Override
    public List<ReservationResourceDto> findReservationByRentalItemId(Integer itemId) {
        return reservationService.findByRentalItemId(itemId)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public List<RentalItemResourceDto> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to) {
        return reservationService.findCanRentedItemAtTerm(from, to)
                .stream()
                .map(RentalItemResourceDto::from)
                .toList();
    }

    @Override
    public boolean canRentedItemAtTerm(Integer itemId, LocalDateTime from, LocalDateTime to) {
        return reservationService.canRentedItemAtTerm(itemId, from, to);
    }

    @Override
    public RentalItemResourceDto addRentalItem(AddRentalItemEventDto addDto) {
        return itemService.add(addDto.toModel())
                .transform(RentalItemResourceDto::from);
    }

    @Override
    public RentalItemResourceDto updateRentalItem(RentalItemResourceDto updateDto) {
        return itemService.update(updateDto.toModel())
                .transform(RentalItemResourceDto::from);
    }

    @Override
    public void deleteRentalItem(Integer itemId) {
        itemService.delete(itemId);
    }

    @Override
    public List<ReservationResourceDto> getAllReservations() {
        return reservationService.getAll()
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Override
    public ReservationResourceDto updateReservation(ReservationResourceDto updateDto) {
        return reservationService.update(updateDto.toModel())
                .transform(ReservationResourceDto::from);
    }

    @Override
    public void deleteReservation(Integer reservationId) {
        reservationService.delete(reservationId);
    }

    @Override
    public List<UserAccountResourceDto> getAllUserAccounts() {
        return userService.getAll()
                .stream()
                .map(UserAccountResourceDto::from)
                .toList();
    }

    @Override
    public UserAccountResourceDto addUserAccount(AddUserAccountEventDto addDto) {
        return userService.add(addDto.toModel())
                .transform(UserAccountResourceDto::from);
    }

    @Override
    public UserAccountResourceDto updateUserAccount(UserAccountResourceDto updateDto) {
        return userService.update(updateDto.toModel())
                .transform(UserAccountResourceDto::from);
    }

    @Override
    public void deleteUserAccount(Integer userAccountId) {
        userService.delete(userAccountId);
    }

    @Override
    public UserAccountResourceDto getOwnUserProfile() {
        var ownUserId = LoginUserUtils.get().getUserId();
        return userService.get(ownUserId)
                .orElseThrow(() -> new BusinessFlowException("UserAccount does not exist for LoginId.",
                        CauseType.NOT_FOUND))
                .transform(UserAccountResourceDto::from);
    }

    @Override
    public UserAccountResourceDto updateUserProfile(UserAccountResourceDto updateDto) {
        if (LoginUserUtils.get().getUserId() != updateDto.getId()) {
            throw new BusinessFlowException("other's profile cannot be updated.", CauseType.FORBIDDEN);
        }
        return userService.update(updateDto.toModel())
                .transform(UserAccountResourceDto::from);
    }
}

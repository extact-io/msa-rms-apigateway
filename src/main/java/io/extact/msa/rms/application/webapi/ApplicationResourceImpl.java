package io.extact.msa.rms.application.webapi;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.application.service.RentalItemAppService;
import io.extact.msa.rms.application.service.ReservationAppService;
import io.extact.msa.rms.application.service.UserAccountAppService;
import io.extact.msa.rms.application.webapi.dto.AddRentalItemEventDto;
import io.extact.msa.rms.application.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.application.webapi.dto.AddUserAccountEventDto;
import io.extact.msa.rms.application.webapi.dto.LoginEventDto;
import io.extact.msa.rms.application.webapi.dto.RentalItemResourceDto;
import io.extact.msa.rms.application.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.application.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.core.jwt.consumer.Authenticated;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserUtils;
import io.extact.msa.rms.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.rms.platform.core.validate.ValidateGroup;
import io.extact.msa.rms.platform.core.validate.ValidateParam;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;

@Path("/rms")
@ApplicationScoped
@ValidateParam
public class ApplicationResourceImpl implements ApplicationResource {

    private ReservationAppService reservationService;
    private RentalItemAppService itemService;
    private UserAccountAppService userService;

    @Inject
    public ApplicationResourceImpl(ReservationAppService reservationService, RentalItemAppService itemService,
            UserAccountAppService userService) {
        this.reservationService = reservationService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(String loginId, String password) {
        return authenticate(LoginEventDto.of(loginId, password)); // this method is for debug so convert.
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(LoginEventDto loginDto) {
        return userService.authenticate(loginDto.getLoginId(), loginDto.getPassword())
                .transform(UserAccountResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByRentalItemAndStartDate(Integer itemId, LocalDate date) {
        return reservationService.findByRentalItemAndStartDate(itemId, date)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByReserverId(Integer reserverId) {
        return reservationService.findByReserverId(reserverId)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> getOwnReservations() {
        return findReservationByReserverId(LoginUserUtils.get().getUserId());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    @Override
    public List<RentalItemResourceDto> getAllRentalItems() {
        return itemService.getAll()
                .stream()
                .map(RentalItemResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @ValidateGroup(groups = Add.class)
    @Override
    public ReservationResourceDto addReservation(AddReservationEventDto addDto) {
        return reservationService.add(addDto.toModel())
                .transform(ReservationResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public void cancelReservation(Integer reservationId) {
        reservationService.cancel(reservationId);
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<ReservationResourceDto> findReservationByRentalItemId(Integer itemId) {
        return reservationService.findByRentalItemId(itemId)
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public List<RentalItemResourceDto> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to) {
        return reservationService.findCanRentedItemAtTerm(from, to)
                .stream()
                .map(RentalItemResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(MEMBER_ROLE)
    @Override
    public boolean canRentedItemAtTerm(Integer itemId, LocalDateTime from, LocalDateTime to) {
        return reservationService.canRentedItemAtTerm(itemId, from, to);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public RentalItemResourceDto addRentalItem(AddRentalItemEventDto addDto) {
        return itemService.add(addDto.toModel())
                .transform(RentalItemResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public RentalItemResourceDto updateRentalItem(RentalItemResourceDto updateDto) {
        return itemService.update(updateDto.toModel())
                .transform(RentalItemResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteRentalItem(Integer itemId) {
        itemService.delete(itemId);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public List<ReservationResourceDto> getAllReservations() {
        return reservationService.getAll()
                .stream()
                .map(ReservationResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public ReservationResourceDto updateReservation(ReservationResourceDto updateDto) {
        return reservationService.update(updateDto.toModel())
                .transform(ReservationResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteReservation(Integer reservationId) {
        reservationService.delete(reservationId);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public List<UserAccountResourceDto> getAllUserAccounts() {
        return userService.getAll()
                .stream()
                .map(UserAccountResourceDto::from)
                .toList();
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto addUserAccount(AddUserAccountEventDto addDto) {
        return userService.add(addDto.toModel())
                .transform(UserAccountResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto updateUserAccount(UserAccountResourceDto updateDto) {
        return userService.update(updateDto.toModel())
                .transform(UserAccountResourceDto::from);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public void deleteUserAccount(Integer userAccountId) {
        userService.delete(userAccountId);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    @Override
    public UserAccountResourceDto getOwnUserProfile() {
        var ownUserId = LoginUserUtils.get().getUserId();
        return userService.get(ownUserId)
                .orElseThrow(() -> new BusinessFlowException("UserAccount does not exist for LoginId.",
                        CauseType.NOT_FOUND))
                .transform(UserAccountResourceDto::from);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    @Override
    public UserAccountResourceDto updateUserProfile(UserAccountResourceDto updateDto) {
        if (LoginUserUtils.get().getUserId() != updateDto.getId()) {
            throw new BusinessFlowException("other's profile cannot be updated.", CauseType.FORBIDDEN);
        }
        return userService.update(updateDto.toModel())
                .transform(UserAccountResourceDto::from);
    }
}

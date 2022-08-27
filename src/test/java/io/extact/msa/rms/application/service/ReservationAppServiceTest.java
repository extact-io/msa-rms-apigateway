package io.extact.msa.rms.application.service;

import static io.extact.msa.rms.application.external.ApiType.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;

import io.extact.msa.rms.application.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.application.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.application.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.application.external.stub.StubUtils;
import io.extact.msa.rms.application.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.application.model.RentalItemModel;
import io.extact.msa.rms.application.model.ReservationModel;
import io.extact.msa.rms.application.model.UserAccountModel;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserUtils;
import io.extact.msa.rms.platform.core.jwt.login.ServiceLoginUser;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddBean(RentalItemApiRemoteStub.class)
@AddBean(ReservationApiRemoteStub.class)
@AddBean(UserAccountApiRemoteStub.class)
@AddBean(ServiceApiRemoteStubApplication.class)
@AddConfig(key = "server.port", value = "7001") // for REST server
@AddConfig(key = "web-api/mp-rest/url", value = "http://localhost:7001") // for REST Client
@AddConfig(key = PROP_NAME, value = REAL)
@ExtendWith(JulToSLF4DelegateExtension.class)
class ReservationAppServiceTest {

    private static final RentalItemModel ITEM_3 = RentalItemModel.of(3, "A0003", "レンタル品3号");

    private static final UserAccountModel USER_1 = UserAccountModel.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
    private static final UserAccountModel USER_2 = UserAccountModel.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER);

    @Inject
    private ReservationAppService reservationService;

    @BeforeEach
    void setup() {
        StubUtils.refreshData();
    }

    @Test
    void testGetAll() {
        var expect = List.of(
                ReservationModel.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1, ITEM_3, USER_1),
                ReservationModel.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2, ITEM_3, USER_2),
                ReservationModel.of(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1, ITEM_3, USER_1)
            );
        var actual = reservationService.getAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testAdd() {
        var addReservation = ReservationModel.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        var expect = ReservationModel.of(4, LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1, ITEM_3, USER_1);
        var actual = reservationService.add(addReservation);
        addReservation.setRentalItem(ITEM_3);
        addReservation.setUserAccount(USER_1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testAddOnItemNotFound() {
        // rentalItemId=999はマスタ登録なし
        var addReservation = ReservationModel.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 999, 1);
        var thrown = catchThrowable(() -> reservationService.add(addReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testAddOnDuplicate() {
        // 2020/4/1 16:00-18:00 で既に予約あり
        var addReservation = ReservationModel.of(null, LocalDateTime.of(2020, 4, 1, 17, 0, 0), LocalDateTime.of(2020, 4, 1, 19, 0, 0), "メモ4", 3, 1);
        var thrown = catchThrowable(() -> reservationService.add(addReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    void testUpdate() {
        var updateReservation = ReservationModel.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1, ITEM_3, USER_1);
        updateReservation.setNote("UPDATE");
        var result = reservationService.update(updateReservation);
        assertThat(result.getNote()).isEqualTo("UPDATE");
        assertThatToString(result).isEqualTo(updateReservation);
    }

    @Test
    void testUpdateOnNotFound() {
        var updateReservation = ReservationModel.of(999, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1, ITEM_3, USER_1);
        var thrown = catchThrowable(() -> reservationService.update(updateReservation));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDelete() {
        var beforeDelete = reservationService.getAll().size();
        reservationService.delete(1);
        assertThat(reservationService.getAll()).hasSize(beforeDelete - 1);
    }

    @Test
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> reservationService.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    // ---------------------------------------

    @Test
    void testFindByRentalItemAndStartDate() {
        var expect = List.of(
                ReservationModel.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1, ITEM_3, USER_1),
                ReservationModel.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2, ITEM_3, USER_2)
            );
        var actual = reservationService.findByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindByRentalItemAndStartDateOnNotFound() {
        var actual = reservationService.findByRentalItemAndStartDate(999, LocalDate.of(2004, 4, 1));
        assertThat(actual).isEmpty();
        actual = reservationService.findByRentalItemAndStartDate(1, LocalDate.of(2004, 7, 10));
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindByReserverId() {
        // 1件ヒットパターン
        var actual = reservationService.findByReserverId(2);
        assertThat(actual).hasSize(1);
        // 2件ヒットパターン
        actual = reservationService.findByReserverId(1);
        assertThat(actual).hasSize(2);
        // 0件ヒットパターン
        actual = reservationService.findByReserverId(3);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindByRentalItemId() {
        // 3件ヒットパターン
        var actual = reservationService.findByRentalItemId(3);
        assertThat(actual).hasSize(3);
        // 0件ヒットパターン
        actual = reservationService.findByRentalItemId(1);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindCanRentedItemAtTerm() {
        // 4/1 9:00-11:00で予約可能なレンタル品 => 1, 2, 4
        var actual = reservationService.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 1, 9, 0), LocalDateTime.of(2020, 4, 1, 11, 0));
        var actualIds = actual.stream().map(RentalItemModel::getId).toList();
        assertThat(actualIds).containsOnly(1, 2, 4);

        // 4/2 9:00-11:00で予約可能なレンタル品 => 1, 2, 3, 4
        actual = reservationService.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 2, 9, 0), LocalDateTime.of(2020, 4, 2, 11, 0));
        actualIds = actual.stream().map(RentalItemModel::getId).toList();
        assertThat(actualIds).containsOnly(1, 2, 3, 4);
    }

    @Test
    void testCanRentedItemAtTerm() {
        // 4/1 13:00-15:00でIDが3のレンタル品がレンタル可能か？
        var actual = reservationService.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 1, 13, 0), LocalDateTime.of(2020, 4, 1, 15, 0));
        assertThat(actual).isTrue();

        // 4/1 9:00-11:00でIDが3のレンタル品がレンタル可能か？
        actual = reservationService.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 1, 9, 0), LocalDateTime.of(2020, 4, 1, 11, 0));
        assertThat(actual).isFalse();
    }

    @Test
    void testCancel() {
        try {
            var invokeMethod = ReflectionUtils.findMethod(LoginUserUtils.class, "set", ServiceLoginUser.class).get();
            ReflectionUtils.invokeMethod(invokeMethod, null, ServiceLoginUser.of(2, Set.of("dummy"))); // set LoginUesr.
            reservationService.cancel(2);
        } finally {
            LoginUserUtils.remove(); // ThreadLocalを使ってるので自分で後始末
        }
    }
}

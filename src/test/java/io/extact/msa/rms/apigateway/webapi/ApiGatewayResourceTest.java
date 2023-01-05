package io.extact.msa.rms.apigateway.webapi;

import static io.extact.msa.rms.apigateway.external.ApiType.*;
import static io.extact.msa.rms.platform.test.PlatformTestUtils.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;

import io.extact.msa.rms.apigateway.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.apigateway.external.stub.StubUtils;
import io.extact.msa.rms.apigateway.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.apigateway.webapi.ApiGatewayResourceTest.LoginUserInterceptor;
import io.extact.msa.rms.apigateway.webapi.dto.AddRentalItemEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddUserAccountEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.RentalItemResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserUtils;
import io.extact.msa.rms.platform.core.jwt.login.ServiceLoginUser;
import io.extact.msa.rms.platform.core.validate.ValidateParam;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * ネットワーク越しにApplicaitonResouceをテストするテストケース。
 * <pre>
 * ・テストドライバ：RestClient(ApplicaitonResouce)
 *     ↓ HTTP
 * ・実物：RestResource(ApplicaitonResouce)
 * ・実物：RestClient(RentalItemApiRestClient/ect.)
 *     ↓ HTTP
 * ・スタブ：RestResource(RentalItemApiRemoteStub/..)
 * </pre>
 */
@HelidonTest
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddBean(RentalItemApiRemoteStub.class)
@AddBean(ReservationApiRemoteStub.class)
@AddBean(UserAccountApiRemoteStub.class)
@AddBean(ServiceApiRemoteStubApplication.class)
@AddConfig(key = PROP_NAME, value = REAL)
@AddConfig(key = "server.port", value = "7001") // for Real(ApplicaitonResouce) and RemoteStub Server port
@AddConfig(key = "jwt.filter.enable", value = "false") // 認証認可OFF
@AddBean(value = LoginUserInterceptor.class, scope = Dependent.class) // test用のユーザを設定するIntercepor定義
@ExtendWith(JulToSLF4DelegateExtension.class)
class ApiGatewayResourceTest {

    private static ServiceLoginUser testUser = ServiceLoginUser.UNKNOWN_USER;
    private ApiGatewayResource endPoint;

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001/rms"))
                .register(RmsTypeParameterFeature.class)
                .build(ApiGatewayResource.class);
        testUser = ServiceLoginUser.UNKNOWN_USER;
        StubUtils.refreshData();
    }

    @Interceptor
    @Priority(Interceptor.Priority.APPLICATION)
    @ValidateParam // @InterceptorBindingとして便宜的に利用
    public static class LoginUserInterceptor {
        @AroundInvoke
        public Object obj(InvocationContext ic) throws Exception {
            try {
                var invokeMethod = ReflectionUtils.findMethod(LoginUserUtils.class, "set", ServiceLoginUser.class).get();
                ReflectionUtils.invokeMethod(invokeMethod, null, testUser); // set LoginUesr.
                return ic.proceed();
            } finally {
                LoginUserUtils.remove();
            }
        }
    }

    @Test
    void testAuthenticate() {
        var expect = userAccountDto1();
        var actual = endPoint.authenticate("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }
    @Test
    void testAuthenticateOnPasswordUnmatch() {// password不一致
        var actual = catchThrowable(() -> endPoint.authenticate("member1", "member9999"));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testAuthenticateOnParameterError() {// id, password桁数不足
        var actual = catchThrowable(() -> endPoint.authenticate("123", "123"));
        assertValidationErrorInfo(actual, 2);
    }


    @Test
    void testFindReservationByRentalItemAndStartDate() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2,
                        rentalItemDto3(), userAccountDto2())
                );
        var actual = endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }
    @Test
    void testFindReservationByRentalItemAndStartDateOnNotFound() {
        var actual = endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2019, 4, 1)); // 該当なし
        assertThat(actual).isEmpty();
    }
    @Test
    void testFindReservationByRentalItemAndStartDateOnParameterError() {// parameter error
        var actual = catchThrowable(
                () -> endPoint.findReservationByRentalItemAndStartDate(-1, LocalDate.of(2020, 4, 1)));
        assertValidationErrorInfo(actual, 1);
    }


    @Test
    void testFindReservationByReserverId() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        var actual = endPoint.findReservationByReserverId(1);
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }
    @Test
    void testFindReservationByReserverIdOnNotFound() {
        var actual = endPoint.findReservationByReserverId(9);
        assertThatToString(actual).isEmpty();
    }
    @Test
    void testFindReservationByReserverIdOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.findReservationByReserverId(-1));
        assertValidationErrorInfo(actual, 1);
    }


    @Test
    void testFindReservationByRentalItemId() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2,
                        rentalItemDto3(), userAccountDto2()),
                newReservationDto(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        var actual = endPoint.findReservationByRentalItemId(3);
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }
    @Test
    void testFindReservationByRentalItemIdOnNotFound() {
        var actual = endPoint.findReservationByRentalItemId(9);
        assertThatToString(actual).isEmpty();
    }
    @Test
    void testFindReservationByRentalItemIdOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.findReservationByRentalItemId(-1));
        assertValidationErrorInfo(actual, 1);
    }


    @Test
    void testFindCanRentedItemAtTerm() {
        var expect = List.of(
                newRentalItemDto(1, "A0001", "レンタル品1号"),
                newRentalItemDto(2, "A0002", "レンタル品2号"),
                newRentalItemDto(4, "A0004", "レンタル品4号")
                );
        var actual = endPoint.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 11, 0, 0));
        assertThatToString(actual).containsExactlyElementsOf(expect); // id=3以外
    }
    @Test
    void testFindCanRentedItemAtTermAllOk() {
        var expect = List.of(
                newRentalItemDto(1, "A0001", "レンタル品1号"),
                newRentalItemDto(2, "A0002", "レンタル品2号"),
                newRentalItemDto(3, "A0003", "レンタル品3号"),
                newRentalItemDto(4, "A0004", "レンタル品4号")
                );
        var actual = endPoint.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0));
        assertThatToString(actual).containsExactlyElementsOf(expect); // 全部OK
    }
    @Test
    void testFindCanRentedItemAtTermOnParameterError() { // parameter error
        var actual = catchThrowable(() -> endPoint.findCanRentedItemAtTerm(null, null));
        assertValidationErrorInfo(actual, 2);
    }


    @Test
    void testCanRentedItemAtTermOk() {
        var actual = endPoint.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0));
        assertThat(actual).isTrue();
    }
    @Test
    void testCanRentedItemAtTermNG() {
        var actual = endPoint.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0));
        assertThat(actual).isFalse();
    }
    @Test
    void testCanRentedItemAtTermOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.canRentedItemAtTerm(-1, null, null));
        assertValidationErrorInfo(actual, 3);
    }


    @Test
    void testGetOwnReservations() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        testUser = ServiceLoginUser.of(1, Set.of("dummy")); // 事前条件の設定(Interceptorで設定するユーザ)
        var actual = endPoint.getOwnReservations();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }


    @Test
    void testGetAllRentalItems() {
        var expect = List.of(
                newRentalItemDto(1, "A0001", "レンタル品1号"),
                newRentalItemDto(2, "A0002", "レンタル品2号"),
                newRentalItemDto(3, "A0003", "レンタル品3号"),
                newRentalItemDto(4, "A0004", "レンタル品4号")
                );
        var actual = endPoint.getAllRentalItems();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }


    @Test
    void testGetAllReservations() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2,
                        rentalItemDto3(), userAccountDto2()),
                newReservationDto(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        var actual = endPoint.getAllReservations();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }


    @Test
    void testGetAllUserAccounts() {
        var expected = List.of(
                newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                newUserAccountResourceDto(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                newUserAccountResourceDto(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actuals = endPoint.getAllUserAccounts();
        assertThatToString(actuals).containsExactlyElementsOf(expected);
    }


    @Test
    void testAddReservation() {
        var expect = newReservationDto(4, LocalDateTime.of(2050, 4, 1, 10, 0, 0), LocalDateTime.of(2050, 4, 1, 12, 0, 0), "メモ4", 3, 1,
                rentalItemDto3(), userAccountDto1());
        var addReservation = newAddReservationDto(LocalDateTime.of(2050, 4, 1, 10, 0, 0), LocalDateTime.of(2050, 4, 1, 12, 0, 0), "メモ4", 3, 1);
        var actual = endPoint.addReservation(addReservation);
        assertThatToString(actual).isEqualTo(expect);
    }
    @Test
    void testAddReservationOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.addReservation(new AddReservationEventDto()));
        assertValidationErrorInfo(actual, 4);
    }
    @Test
    void testAddReservationOnCorrelationCheckError() {// startDateTime < EndDateTime エラー
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 12, 0, 0), LocalDateTime.of(2099, 4, 1, 10, 0, 0), "メモ4", 3, 1);
        var actual = catchThrowable(() -> endPoint.addReservation(addReservation));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testAddReservationOnNotFound() {// 該当なし
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ4", 999, 1);
        var actual = catchThrowable(() -> endPoint.addReservation(addReservation));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testAddReservationOnDuplicate() {// 期間重複
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 9, 0, 0), LocalDateTime.of(2099, 4, 1, 11, 0, 0), "メモ4", 3, 1); // 期間重複あり
        var actual = catchThrowable(() -> endPoint.addReservation(addReservation));
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class, CauseType.DUPRICATE);
    }


    @Test
    void testAddRentalItem() {
        var expect = newRentalItemDto(5, "A0005", "レンタル品5号");
        var addRentalItem = newAddRentalItemDto("A0005", "レンタル品5号");
        var actual = endPoint.addRentalItem(addRentalItem);
        assertThatToString(actual).isEqualTo(expect);
    }
    @Test
    void testAddRentalItemOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.addRentalItem(new AddRentalItemEventDto()));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testAddRentalItemOnDuplicateData() {
        var addRentalItem = newAddRentalItemDto("A0004", "レンタル品5号"); // SerialNo重複
        var actual = catchThrowable(() -> endPoint.addRentalItem(addRentalItem));
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class, CauseType.DUPRICATE);
    }


    @Test
    void testAddUserAccount() {
        var expect = newUserAccountResourceDto(4, "member3", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER);
        var addUserAccount = newAddUserAccountDto("member3", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER);
        var actual = endPoint.addUserAccount(addUserAccount);
        assertThatToString(actual).isEqualTo(expect);
    }
    @Test
    void testAddUserAccountOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.addUserAccount(new AddUserAccountEventDto()));
        assertValidationErrorInfo(actual, 4); // 未入力4件
    }
    @Test
    void testAddUserAccountOnDuplicate() {
        var addUserAccount = newAddUserAccountDto("member2", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER); // loginId重複
        var actual = catchThrowable(() -> endPoint.addUserAccount(addUserAccount));
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class, CauseType.DUPRICATE);
    }


    @Test
    void testCancelReservation() {
        testUser = ServiceLoginUser.of(2, Set.of("dummy")); // 事前条件の設定(Interceptorで設定するユーザ)
        var ownReservationSize = endPoint.findReservationByReserverId(2).size();
        endPoint.cancelReservation(2); // reservation.id=2, reservation.userAccountId=2を削除
        var ownReservations = endPoint.findReservationByReserverId(2);
        assertThatToString(ownReservations).hasSize(ownReservationSize -1); // 1件削除されていること
    }
    @Test
    void testCancelReservationOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.cancelReservation(-1));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testCancelReservationOnNotFound() {// 該当なし
        var actual = catchThrowable(() -> endPoint.cancelReservation(999));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testCancelReservationOnForbidden() {
        // 事前条件の設定(Interceptorで設定するユーザ)
        testUser = ServiceLoginUser.of(2, Set.of("dummy"));
        // reservation.id=1の予約者はid=1のユーザなので消せないハズ
        var actual = catchThrowable(() -> endPoint.cancelReservation(1));
        assertGenericErrorInfo(actual, Status.FORBIDDEN, BusinessFlowException.class, CauseType.FORBIDDEN);
    }


    @Test
    void testUpdateRentalItem() {
        var update = newRentalItemDto(2, "UPDATE-1", "UPDATE-2");
        var actual = endPoint.updateRentalItem(update);
        assertThatToString(actual).isEqualTo(update);
    }
    @Test
    void testUpdateRentalItemOnNotFound() {// 該当なし
        var update = newRentalItemDto(9, "UPDATE-1", "UPDATE-2");
        var actual = catchThrowable(() -> endPoint.updateRentalItem(update));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testUpdateRentalItemOnParameterError() {// parameter error
        var update = newRentalItemDto(null, "@@@@@", "1234567890123456");
        var actual = catchThrowable(() -> endPoint.updateRentalItem(update));
        assertValidationErrorInfo(actual, 3); // 未入力3件
    }

    @Test
    void testUpdateReservation() {
        var update = newReservationDto(3, LocalDateTime.of(2099, 1, 1, 0, 0), LocalDateTime.of(2099, 12, 31, 23, 59), "update", 1, 2, null, null);
        var actual = endPoint.updateReservation(update);
        update.setRentalItemDto(rentalItemDto1());
        update.setUserAccountDto(userAccountDto2());
        assertThatToString(actual).isEqualTo(update);
    }
    @Test
    void testUpdateReservationOnNotFound() {// 該当なし
        var update = newReservationDto(999, LocalDateTime.of(2099, 1, 1, 0, 0), LocalDateTime.of(2099, 12, 31, 23, 59), "update", 1, 1, null, null);
        var actual = catchThrowable(() -> endPoint.updateReservation(update));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testUpdateReservationOnParameterError() {// parameter error
        var update = newReservationDto(-1, null, null, "update", -1, -1, null, null);
        var actual = catchThrowable(() -> endPoint.updateReservation(update));
        assertValidationErrorInfo(actual, 5); // 未入力3件
    }


    @Test
    void testUpdateUserAccount() {
        var updateUser = newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = endPoint.updateUserAccount(updateUser);
        assertThatToString(actual).isEqualTo(updateUser);
    }
    @Test
    void testUpdateUserAccountOnNotFound() {// 該当なし
        var updateUser = newUserAccountResourceDto(999, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = catchThrowable(() -> endPoint.updateUserAccount(updateUser));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testUpdateUserAccountOnParameterError() {// parameter error
        var updateUser = newUserAccountResourceDto(null, null, null, "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = catchThrowable(() -> endPoint.updateUserAccount(updateUser));
        assertValidationErrorInfo(actual, 3); // 未入力3件
    }


    @Test
    void testDeleteRentalItem() {
        var beforeSize = endPoint.getAllRentalItems().size();
        endPoint.deleteRentalItem(4);
        var afterSize = endPoint.getAllRentalItems().size();
        assertThat(afterSize).isEqualTo(beforeSize - 1); // 1件削除されていること
    }
    @Test
    void testDeleteRentalItemOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.deleteRentalItem(-1));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testDeleteRentalItemOnNotFound() {// 該当なし
        var actual = catchThrowable(() -> endPoint.deleteRentalItem(999));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testDeleteRentalItemOnRefered() {// 予約から参照されている
        var actual = catchThrowable(() -> endPoint.deleteRentalItem(3));
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class, CauseType.REFERED);
    }


    @Test
    void testDeleteReservation() {
        var beforeSize = endPoint.getAllReservations().size();
        endPoint.deleteReservation(3);
        var afterSize = endPoint.getAllReservations().size();
        assertThat(afterSize).isEqualTo(beforeSize - 1); // 1件削除されていること
    }
    @Test
    void testDeleteReservationParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.deleteReservation(-1));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testDeleteReservationOnNotFound() {// 該当なし
        var actual = catchThrowable(() -> endPoint.deleteReservation(999));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }


    @Test
    void testDeleteUserAccount() {
        var beforeSize = endPoint.getAllUserAccounts().size();
        endPoint.deleteUserAccount(3);
        var afterSize = endPoint.getAllUserAccounts().size();
        assertThat(afterSize).isEqualTo(beforeSize - 1); // 1件削除されていること
    }
    @Test
    void testDeleteUserAccountOnParameterError() {// parameter error
        var actual = catchThrowable(() -> endPoint.deleteUserAccount(-1));
        assertValidationErrorInfo(actual, 1);
    }
    @Test
    void testDeleteUserAccountOnNotFound() {// 該当なし
        var actual = catchThrowable(() -> endPoint.deleteUserAccount(999));
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class, CauseType.NOT_FOUND);
    }
    @Test
    void testDeleteUserAccountOnRefered() {// 予約から参照されている
        var actual = catchThrowable(() -> endPoint.deleteUserAccount(1));
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class, CauseType.REFERED);
    }

    @Test
    void testGetOwnProfile() {
        testUser = ServiceLoginUser.of(1, Set.of("dummy")); // 事前条件の設定(Interceptorで設定するユーザ)
        var actual = endPoint.getOwnUserProfile();
        var expected = newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    void testUpdateOwnProfile() {
        testUser = ServiceLoginUser.of(1, Set.of("dummy")); // 事前条件の設定(Interceptorで設定するユーザ)
        var updateUser = newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = endPoint.updateUserProfile(updateUser);
        assertThatToString(actual).isEqualTo(updateUser);
    }
    @Test
    void testUpdateOwnProfileOnForbidden() {
        testUser = ServiceLoginUser.of(1, null); // 事前条件の設定(Interceptorで設定するユーザ)
        var updateUser = newUserAccountResourceDto(999, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = catchThrowable(() -> endPoint.updateUserProfile(updateUser)); // 該当なし
        assertGenericErrorInfo(actual, Status.FORBIDDEN, BusinessFlowException.class, CauseType.FORBIDDEN);
    }
    @Test
    void testUpdateOwnProfileOnParameterError() {// parameter error
        var updateUser = newUserAccountResourceDto(null, null, null, "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = catchThrowable(() -> endPoint.updateUserProfile(updateUser));
        assertValidationErrorInfo(actual, 3); // 未入力3件
    }


    // ------------------------------------------------------------ private utils methods.

    private ReservationResourceDto newReservationDto(Integer id, LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            Integer rentalItemId, Integer userAccountId, RentalItemResourceDto rentalItemDto, UserAccountResourceDto userAccountDto) {
        var dto = new ReservationResourceDto();
        dto.setId(id);
        dto.setStartDateTime(startDateTime);
        dto.setEndDateTime(endDateTime);
        dto.setNote(note);
        dto.setRentalItemId(rentalItemId);
        dto.setUserAccountId(userAccountId);
        dto.setRentalItemDto(rentalItemDto);
        dto.setUserAccountDto(userAccountDto);
        return dto;
    }

    private RentalItemResourceDto newRentalItemDto(Integer id, String serialNo, String itemName) {
        var dto = new RentalItemResourceDto();
        dto.setId(id);
        dto.setSerialNo(serialNo);
        dto.setItemName(itemName);
        return dto;
    }

    private UserAccountResourceDto newUserAccountResourceDto(Integer id, String loginId, String password, String userName, String phoneNumber,
            String contact, UserType userType) {
        var dto = new UserAccountResourceDto();
        dto.setId(id);
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setUserName(userName);
        dto.setPhoneNumber(phoneNumber);
        dto.setContact(contact);
        dto.setUserType(userType.name());
        return dto;
    }

    private AddReservationEventDto newAddReservationDto(LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            Integer rentalItemId, Integer userAccountId) {
        var dto = new AddReservationEventDto();
        dto.setStartDateTime(startDateTime);
        dto.setEndDateTime(endDateTime);
        dto.setNote(note);
        dto.setRentalItemId(rentalItemId);
        dto.setUserAccountId(userAccountId);
        return dto;
    }

    private AddRentalItemEventDto newAddRentalItemDto(String serialNo, String itemName) {
        var dto = new AddRentalItemEventDto();
        dto.setSerialNo(serialNo);
        dto.setItemName(itemName);
        return dto;
    }

    private AddUserAccountEventDto newAddUserAccountDto(String loginId, String password, String userName, String phoneNumber, String contact, UserType userType) {
        var dto = new AddUserAccountEventDto();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setUserName(userName);
        dto.setPhoneNumber(phoneNumber);
        dto.setContact(contact);
        dto.setUserType(userType);
        return dto;
    }

    private RentalItemResourceDto rentalItemDto1() {
        return newRentalItemDto(1, "A0001", "レンタル品1号");
    }

    private RentalItemResourceDto rentalItemDto3() {
        return newRentalItemDto(3, "A0003", "レンタル品3号");
    }

    private UserAccountResourceDto userAccountDto1() {
        return newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
    }

    private UserAccountResourceDto userAccountDto2() {
        return newUserAccountResourceDto(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER);
    }
}

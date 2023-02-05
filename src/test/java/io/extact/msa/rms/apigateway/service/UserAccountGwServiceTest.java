package io.extact.msa.rms.apigateway.service;

import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.extact.msa.rms.apigateway.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.apigateway.external.stub.StubUtils;
import io.extact.msa.rms.apigateway.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.apigateway.model.UserAccountModel;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddBean(RentalItemApiRemoteStub.class)
@AddBean(ReservationApiRemoteStub.class)
@AddBean(UserAccountApiRemoteStub.class)
@AddBean(ServiceApiRemoteStubApplication.class)
@AddConfig(key = "server.port", value = "7001") // for REST server
@AddConfig(key = "web-api/mp-rest/url", value = "http://localhost:7001") // for REST Client
@ExtendWith(JulToSLF4DelegateExtension.class)
class UserAccountGwServiceTest {

    @Inject
    private UserAccountGwService userService;

    @BeforeEach
    void setup() {
        StubUtils.refreshData();
    }

    @Test
    void testGetAll() {
        var expect = List.of(
                UserAccountModel.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccountModel.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                UserAccountModel.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
            );
        var actual = userService.getAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testGet() {
        var except = UserAccountModel.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = userService.get(1);
        assertThatToString(actual.get()).isEqualTo(except);
    }

    @Test
    void testGetOnNotFound() {
        var actual = userService.get(999);
        assertThat(actual).isEmpty();
    }


    @Test
    void testAdd() {
        var addUser = UserAccountModel.ofTransient("member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var expect = UserAccountModel.of(4, "member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var actual = userService.add(addUser);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testAddOnDuplicate() {
        var addUser = UserAccountModel.ofTransient("member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER); // "member1"のloginIdは既に登録済み
        var thrown = catchThrowable(() -> userService.add(addUser));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    void testUpdate() {
        var updateUser = UserAccountModel.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        updateUser.setUserName("UPDATE");
        var result = userService.update(updateUser);
        assertThat(result.getUserName()).isEqualTo("UPDATE");
        assertThatToString(result).isEqualTo(updateUser);
    }

    @Test
    void testUpdateOnNotFound() {
        var updateUser = UserAccountModel.of(999, "member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var thrown = catchThrowable(() -> userService.update(updateUser));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDelete() {
        var beforeDelete = userService.getAll().size();
        userService.delete(3);
        assertThat(userService.getAll()).hasSize(beforeDelete - 1);
    }

    @Test
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> userService.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDeleteOnRefered() {
        var thrown = catchThrowable(() -> userService.delete(1));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.REFERED);
    }

    @Test
    void testAuthenticate() {
        var expect = UserAccountModel.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = userService.authenticate("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource({ "soramame, hoge", "hoge, soramame", "hoge, hoge" })
    void testAuthenticateOnFail(String id, String password) {
        var thrown = catchThrowable(() -> userService.authenticate(id, password));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }
}

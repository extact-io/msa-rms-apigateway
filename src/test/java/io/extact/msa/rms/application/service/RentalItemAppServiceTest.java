package io.extact.msa.rms.application.service;

import static io.extact.msa.rms.application.external.ApiType.*;
import static io.extact.msa.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.msa.rms.application.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.application.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.application.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.application.external.stub.StubUtils;
import io.extact.msa.rms.application.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.application.model.RentalItemModel;
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
class RentalItemAppServiceTest {

    @Inject
    private RentalItemAppService itemService;

    @BeforeEach
    void setup() {
        StubUtils.refreshData();
    }

    @Test
    void testGetAll() {
        var expect = List.of(
                RentalItemModel.of(1, "A0001", "レンタル品1号"),
                RentalItemModel.of(2, "A0002", "レンタル品2号"),
                RentalItemModel.of(3, "A0003", "レンタル品3号"),
                RentalItemModel.of(4, "A0004", "レンタル品4号")
            );
        var actual = itemService.getAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testAdd() {
        var addItem = RentalItemModel.ofTransient("A0005", "レンタル品5号");
        var expect = RentalItemModel.of(5, "A0005", "レンタル品5号");
        var actual = itemService.add(addItem);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testAddOnDuplicate() {
        var addItem = RentalItemModel.ofTransient("A0004", "レンタル品5号"); // "A0004"は既に登録済みのSerialNo
        var thrown = catchThrowable(() -> itemService.add(addItem));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.DUPRICATE);
    }

    @Test
    void testUpdate() {
        var updateItem = RentalItemModel.of(1, "A0001", "レンタル品1号");
        updateItem.setItemName("UPDATE");
        var result = itemService.update(updateItem);
        assertThat(result.getItemName()).isEqualTo("UPDATE");
        assertThatToString(result).isEqualTo(updateItem);
    }

    @Test
    void testUpdateOnNotFound() {
        var update = RentalItemModel.of(999, null, null);
        var thrown = catchThrowable(() -> itemService.update(update));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDelete() {
        var beforeDelete = itemService.getAll().size();
        itemService.delete(1);
        assertThat(itemService.getAll()).hasSize(beforeDelete - 1);
    }

    @Test
    void testDeleteOnNotFound() {
        var thrown = catchThrowable(() -> itemService.delete(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testDeleteOnRefered() {
        var thrown = catchThrowable(() -> itemService.delete(3));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.REFERED);
    }
}

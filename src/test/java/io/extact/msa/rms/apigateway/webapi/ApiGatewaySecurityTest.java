package io.extact.msa.rms.apigateway.webapi;

import static io.extact.msa.rms.apigateway.external.ApiType.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.extact.msa.rms.apigateway.external.stub.RentalItemApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ReservationApiRemoteStub;
import io.extact.msa.rms.apigateway.external.stub.ServiceApiRemoteStubApplication;
import io.extact.msa.rms.apigateway.external.stub.UserAccountApiRemoteStub;
import io.extact.msa.rms.apigateway.webapi.dto.AddRentalItemEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddUserAccountEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.RentalItemResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.core.jaxrs.converter.RmsTypeParameterFeature;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.test.junit5.JulToSLF4DelegateExtension;
import io.extact.msa.rms.test.utils.ClearOpenTelemetryContextCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = false)
@AddExtension(ClearOpenTelemetryContextCdiExtension.class)
@AddBean(RentalItemApiRemoteStub.class)
@AddBean(ReservationApiRemoteStub.class)
@AddBean(UserAccountApiRemoteStub.class)
@AddBean(ServiceApiRemoteStubApplication.class)
@AddConfig(key = PROP_NAME, value = REAL)
@AddConfig(key = "server.port", value = "7001") // for REST server
@AddConfig(key = "web-api/mp-rest/url", value = "http://localhost:7001") // for REST Client
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class ApiGatewaySecurityTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGatewaySecurityTest.class);
    private static String authHeaderValue;

    private ApiGatewayResource endPoint;

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001/rms"))
                .register(RmsTypeParameterFeature.class)
                .register(JwtRoleSenderClientFilter.class)
                .build(ApiGatewayResource.class);
    }

    @AfterEach
    void teardown() {
        authHeaderValue = null;
    }

    // Register by RestClientBuilder#register()
    @ConstrainedTo(RuntimeType.CLIENT)
    public static class JwtRoleSenderClientFilter implements ClientRequestFilter, ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            if (StringUtils.isEmpty(authHeaderValue)) {
                return;
            }
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                LOG.info("Authorizationなし");
                return;
            }
            ApiGatewaySecurityTest.authHeaderValue = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }

    @Test
    void testNotAuthenticateStateCall() {

        WebApplicationException actual = null;
        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByReserverId(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getOwnReservations(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemId(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findCanRentedItemAtTerm(LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.canRentedItemAtTerm(3, LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getAllRentalItems(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getAllReservations(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getAllUserAccounts(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.addReservation(newAddReservationDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.addRentalItem(newAddRentalItemDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.addUserAccount(newAddUserAccountDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateRentalItem(newRentalItemResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateReservation(newReservationResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateUserAccount(newUserAccountResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteRentalItem(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteReservation(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteUserAccount(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.cancelReservation(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getOwnUserProfile(),
                    WebApplicationException.class);
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateUserProfile(newUserAccountResourceDto()),
                    WebApplicationException.class);
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testAuthenticatedStateCallAndRoleAllows() {

        // as MEMBER
        assertThatCode(() -> {
                endPoint.authenticate("member1", "member1");
                endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
                endPoint.findReservationByReserverId(1);
                endPoint.getOwnReservations();
                endPoint.getAllRentalItems();
                endPoint.findReservationByRentalItemId(1);
                endPoint.findCanRentedItemAtTerm(LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0));
                endPoint.canRentedItemAtTerm(3, LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0));
                endPoint.addReservation(newAddReservationDto());
                endPoint.cancelReservation(4); // data of addReservation()
                endPoint.getOwnUserProfile();
                endPoint.updateUserProfile(newUserAccountResourceDto());
        }).doesNotThrowAnyException();

        // as ADMIN
        assertThatCode(() -> {
                endPoint.authenticate("admin", "admin");
                endPoint.addRentalItem(newAddRentalItemDto());
                endPoint.addUserAccount(newAddUserAccountDto());
                endPoint.getAllRentalItems();
                endPoint.getAllReservations();
                endPoint.getAllUserAccounts();
                endPoint.updateRentalItem(newRentalItemResourceDto());
                endPoint.updateReservation(newReservationResourceDto());
                endPoint.updateUserAccount(newUserAccountResourceDto());
                endPoint.deleteRentalItem(1);
                endPoint.deleteReservation(1);
                var updateDto = newUserAccountResourceDto();
                updateDto.setId(3);
                updateDto.setLoginId("member3");
                endPoint.getOwnUserProfile();
                endPoint.updateUserProfile(updateDto);
                endPoint.deleteUserAccount(3);
        }).doesNotThrowAnyException();
    }

    @Test
    void testDenyRolesAsMember() {

        endPoint.authenticate("member1", "member1"); // as MEMBER

        // methods that MEMBER can't call
        WebApplicationException actual = null;
        actual = catchThrowableOfType(() ->
                    endPoint.addRentalItem(newAddRentalItemDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.addUserAccount(newAddUserAccountDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getAllReservations(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getAllUserAccounts(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateRentalItem(newRentalItemResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateReservation(newReservationResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.updateUserAccount(newUserAccountResourceDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteRentalItem(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteReservation(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.deleteUserAccount(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void testDenyRolesAsAdmin() {

        endPoint.authenticate("admin", "admin"); // as ADMIN

        // methods that ADMIN can't call
        WebApplicationException actual = null;
        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByReserverId(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.getOwnReservations(),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemId(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.findCanRentedItemAtTerm(LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.canRentedItemAtTerm(3, LocalDateTime.of(2021, 4, 1, 12, 0), LocalDateTime.of(2021, 4, 1, 13, 0)),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.addReservation(newAddReservationDto()),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        actual = catchThrowableOfType(() ->
                    endPoint.cancelReservation(1),
                    WebApplicationException.class
                    );
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
    }

    private AddReservationEventDto newAddReservationDto() {
        var dto = new AddReservationEventDto();
        dto.setStartDateTime(LocalDateTime.of(2050, 4, 1, 10, 00));
        dto.setEndDateTime(LocalDateTime.of(2050, 4, 1, 12, 00));
        dto.setNote("メモ9");
        dto.setRentalItemId(3);
        dto.setUserAccountId(1);
        return dto;
    }

    private AddRentalItemEventDto newAddRentalItemDto() {
        var dto = new AddRentalItemEventDto();
        dto.setSerialNo("TEMP0001");
        dto.setItemName("レンタル品");
        return dto;
    }

    private AddUserAccountEventDto newAddUserAccountDto() {
        var dto = new AddUserAccountEventDto();
        dto.setLoginId("member9");
        dto.setPassword("password9");
        dto.setUserName("ユーザ999");
        dto.setPhoneNumber("090-9999-9999");
        dto.setContact("連絡先999");
        dto.setUserType(UserType.MEMBER);
        return dto;
    }

    private RentalItemResourceDto newRentalItemResourceDto() {
        var dto = new RentalItemResourceDto();
        dto.setId(1);
        dto.setSerialNo("TEMP9999");
        dto.setItemName("レンタル品");
        return dto;
    }

    private ReservationResourceDto newReservationResourceDto() {
        var dto = new ReservationResourceDto();
        dto.setId(1);
        dto.setStartDateTime(LocalDateTime.of(2099, 4, 1, 10, 00));
        dto.setEndDateTime(LocalDateTime.of(2099, 4, 1, 12, 00));
        dto.setNote("メモ9");
        dto.setRentalItemId(3);
        dto.setUserAccountId(1);
        return dto;
    }

    private UserAccountResourceDto newUserAccountResourceDto() {
        var dto = new UserAccountResourceDto();
        dto.setId(1);
        dto.setLoginId("member1");
        dto.setPassword("member1");
        dto.setUserName("メンバー1");
        dto.setPhoneNumber("070-1111-2222");
        dto.setContact("連絡先1");
        dto.setUserType(UserType.MEMBER.name());
        return dto;
    }
}
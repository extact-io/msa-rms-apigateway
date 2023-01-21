package io.extact.msa.rms.apigateway.external.stub;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;

import io.extact.msa.rms.platform.fw.login.LoginUserFromHttpHeadersRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationPath("api")
public class ServiceApiRemoteStubApplication extends RmsApplication {
    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                LoginUserFromHttpHeadersRequestFilter.class,
                RentalItemApiRemoteStub.class,
                ReservationApiRemoteStub.class,
                UserAccountApiRemoteStub.class
                );
    }
}

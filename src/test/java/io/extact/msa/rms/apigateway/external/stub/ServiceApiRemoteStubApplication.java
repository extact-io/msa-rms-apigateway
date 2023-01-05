package io.extact.msa.rms.apigateway.external.stub;

import java.util.Set;

import io.extact.msa.rms.platform.core.jwt.login.PropagatedLoginHeaderRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;


public class ServiceApiRemoteStubApplication extends RmsApplication {
    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                PropagatedLoginHeaderRequestFilter.class,
                RentalItemApiRemoteStub.class,
                ReservationApiRemoteStub.class,
                UserAccountApiRemoteStub.class
                );
    }
}

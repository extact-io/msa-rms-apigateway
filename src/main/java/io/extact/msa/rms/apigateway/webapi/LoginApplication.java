package io.extact.msa.rms.apigateway.webapi;

import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;

import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@ApplicationPath("auth")
@ApplicationScoped
public class LoginApplication extends RmsApplication {
    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    JwtProvideResponseFilter.class,
                    LoginResourceImpl.class
                );
    }
    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
                    // The following keys are defined in `ServerProperties.BV_SEND_ERROR_IN_RESPONSE`
                    "jersey.config.beanValidation.disable.server", true  // jerseyのJAX-RSのBeanValidationサポートをOFFにする
                );
    }
}

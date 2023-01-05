package io.extact.msa.rms.apigateway.webapi;

import java.util.Map;
import java.util.Set;

import io.extact.msa.rms.platform.core.jwt.JwtSecurityFilterFeature;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserRequestFilter;
import io.extact.msa.rms.platform.core.role.RoleSecurityDynamicFeature;
import io.extact.msa.rms.platform.fw.webapi.server.ManagementResource;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApiGatewayApplication extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                JwtSecurityFilterFeature.class,
                RoleSecurityDynamicFeature.class,
                LoginUserRequestFilter.class,
                ManagementResource.class,
                ApiGatewayResourceImpl.class
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

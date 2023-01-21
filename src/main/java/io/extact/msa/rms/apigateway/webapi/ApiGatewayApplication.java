package io.extact.msa.rms.apigateway.webapi;

import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;

import org.eclipse.microprofile.auth.LoginConfig;

import io.extact.msa.rms.platform.fw.login.LoginUserFromJwtRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.ManagementResource;
import io.extact.msa.rms.platform.fw.webapi.RmsApplication;

@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("api")
@ApplicationScoped
public class ApiGatewayApplication extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    LoginUserFromJwtRequestFilter.class,
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

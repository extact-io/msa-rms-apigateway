package io.extact.msa.rms.apigateway.webapi;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

import io.extact.msa.rms.platform.fw.login.LoginUserFromJwtRequestFilter;
import io.extact.msa.rms.platform.fw.webapi.ManagementResource;
import io.extact.msa.rms.platform.fw.webapi.RmsBaseApplications;

@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("api")
@ApplicationScoped
public class ApiGatewayApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.addAll(RmsBaseApplications.CLASSES);
        classes.addAll(getWebApiClasses());
        return classes;
    }

    @Override
    public Map<String, Object> getProperties() {
        return RmsBaseApplications.PROPERTIES;
    }

    private Set<Class<?>> getWebApiClasses() {
        return Set.of(
                    LoginUserFromJwtRequestFilter.class,
                    ManagementResource.class,
                    ApiGatewayResourceImpl.class
                );
    }
}

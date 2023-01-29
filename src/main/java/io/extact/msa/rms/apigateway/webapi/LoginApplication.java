package io.extact.msa.rms.apigateway.webapi;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;
import io.extact.msa.rms.platform.fw.webapi.RmsBaseApplications;

@ApplicationPath("auth")
@ApplicationScoped
public class LoginApplication extends Application {

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
                    JwtProvideResponseFilter.class,
                    LoginResourceImpl.class
                );
    }
}

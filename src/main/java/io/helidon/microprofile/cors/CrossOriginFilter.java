package io.helidon.microprofile.cors;

import java.util.Optional;

import io.helidon.microprofile.cors.CorsSupportMp.RequestAdapterMp;
import io.helidon.microprofile.cors.CorsSupportMp.ResponseAdapterMp;
import io.helidon.webserver.cors.CrossOriginConfig;
import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

/**
 * Class CrossOriginFilter.
 */
// TODO: workaround for https://github.com/helidon-io/helidon/issues/6787 
@Priority(Priorities.AUTHENTICATION - 100)
//@Priority(Priorities.HEADER_DECORATOR)
class CrossOriginFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private ResourceInfo resourceInfo;

    private CorsCdiExtension corsCdiExtension;

    CrossOriginFilter() {
        corsCdiExtension = CDI.current().getBeanManager().getExtension(CorsCdiExtension.class);
        corsCdiExtension.recordSupplierOfCrossOriginConfigFromAnnotation(this::crossOriginConfigFromAnnotationSupplier);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Optional<Response> response = corsCdiExtension.corsSupportMp()
                .processRequest(new RequestAdapterMp(requestContext), new ResponseAdapterMp());
        response.ifPresent(requestContext::abortWith);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        corsCdiExtension.corsSupportMp()
                .prepareResponse(new RequestAdapterMp(requestContext), new ResponseAdapterMp(responseContext));
    }

    Optional<CrossOriginConfig> crossOriginConfigFromAnnotationSupplier() {
        return corsCdiExtension.crossOriginConfig(resourceInfo.getResourceMethod());
    }
}

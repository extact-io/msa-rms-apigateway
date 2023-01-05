package io.extact.msa.rms.apigateway.external.proxy;

import static io.extact.msa.rms.apigateway.external.ApiType.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.apigateway.external.RentalItemApi;
import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;
import io.extact.msa.rms.apigateway.external.restclient.RentalItemApiRestClient;
import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
public class RentalItemApiProxy implements RentalItemApi {

    private RentalItemApiRestClient client;

    @Inject
    public RentalItemApiProxy(@RestClient RentalItemApiRestClient client) {
        this.client = client;
    }

    @Override
    public List<RentalItemDto> getAll() {
        return client.getAll();
    }

    @Override
    public CompletableFuture<RentalItemDto> getAsync(int itemId) {
        return client.getAsync(itemId).toCompletableFuture();
    }

    @Override
    public RentalItemDto add(AddRentalItemDto dto) {
        return client.add(dto);
    }

    @Override
    public RentalItemDto update(RentalItemDto dto) {
        return client.update(dto);
    }

    @Override
    public void delete(int itemId) {
        client.delete(itemId);
    }
}

package io.extact.msa.rms.application.external.bridge;

import static io.extact.msa.rms.application.external.ApiType.*;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.application.external.RentalItemApi;
import io.extact.msa.rms.application.external.dto.AddRentalItemDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;
import io.extact.msa.rms.application.external.restclient.RentalItemApiRestClient;
import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
public class RentalItemApiRestBridge implements RentalItemApi {

    private RentalItemApiRestClient client;

    @Inject
    public RentalItemApiRestBridge(@RestClient RentalItemApiRestClient client) {
        this.client = client;
    }

    @Override
    public List<RentalItemDto> getAll() {
        return client.getAll();
    }

    @Override
    public Optional<RentalItemDto> get(int itemId) {
        return Optional.ofNullable(client.get(itemId));
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

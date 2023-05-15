package io.extact.msa.rms.apigateway.external.proxy;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.apigateway.external.RentalItemApi;
import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;
import io.extact.msa.rms.apigateway.external.restclient.RentalItemApiRestClient;

@ApplicationScoped
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
    public RentalItemDto get(int itemId) {
        return client.get(itemId);
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

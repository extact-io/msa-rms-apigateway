package io.extact.msa.rms.apigateway.external.stub;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;
import io.extact.msa.rms.apigateway.external.restclient.RentalItemApiRestClient;
import io.extact.msa.rms.platform.test.stub.RentalItemMemoryStub;
import io.extact.msa.rms.platform.test.stub.dto.AddRentalItemStubDto;
import io.extact.msa.rms.platform.test.stub.dto.RentalItemStubDto;

@ApplicationScoped
@Path("/items")
public class RentalItemApiRemoteStub implements RentalItemApiRestClient {

    private RentalItemMemoryStub stub = new RentalItemMemoryStub();

    @PostConstruct
    public void init() {
        stub.init();
    }

    @Override
    public List<RentalItemDto> getAll() {
        return stub.getAll().stream()
                .map(this::convertRentalItemDto)
                .toList();
    }

    @Override
    public CompletionStage<RentalItemDto> getAsync(Integer itemId) {
        return CompletableFuture.supplyAsync(() -> this.get(itemId));
    }

    @Override
    public RentalItemDto add(AddRentalItemDto dto) {
        return stub.add(convertAddRentalItemDto(dto))
                .transform(this::convertRentalItemDto);
    }

    @Override
    public RentalItemDto update(RentalItemDto dto) {
        return stub.update(convertRentalItemStubDto(dto)).transform(this::convertRentalItemDto);
    }

    @Override
    public void delete(Integer itemId) {
        stub.delete(itemId);
    }


    // ----------------------------------------------------- convert methods

    private RentalItemDto get(Integer itemId) {
        return stub.get(itemId)
                .map(this::convertRentalItemDto)
                .orElse(null);
    }

    private RentalItemDto convertRentalItemDto(RentalItemStubDto src) {
        return RentalItemDto.of(src.getId(), src.getSerialNo(), src.getItemName());
    }
    private RentalItemStubDto convertRentalItemStubDto(RentalItemDto src) {
        return RentalItemStubDto.of(src.getId(), src.getSerialNo(), src.getItemName());
    }
    private AddRentalItemStubDto convertAddRentalItemDto(AddRentalItemDto src) {
        return AddRentalItemStubDto.of(src.getSerialNo(), src.getItemName());
    }
}

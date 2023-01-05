package io.extact.msa.rms.apigateway.external;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;

public interface RentalItemApi {
    List<RentalItemDto> getAll();

    CompletableFuture<RentalItemDto> getAsync(int itemId);

    RentalItemDto add(AddRentalItemDto dto);

    RentalItemDto update(RentalItemDto dto);

    void delete(int itemId);
}

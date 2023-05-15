package io.extact.msa.rms.apigateway.external;

import java.util.List;

import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;

public interface RentalItemApi {
    List<RentalItemDto> getAll();

    RentalItemDto get(int itemId);

    RentalItemDto add(AddRentalItemDto dto);

    RentalItemDto update(RentalItemDto dto);

    void delete(int itemId);
}

package io.extact.msa.rms.application.external;

import java.util.List;
import java.util.Optional;

import io.extact.msa.rms.application.external.dto.AddRentalItemDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;

public interface RentalItemApi {
    List<RentalItemDto> getAll();

    Optional<RentalItemDto> get(int itemId);

    RentalItemDto add(AddRentalItemDto dto);

    RentalItemDto update(RentalItemDto dto);

    void delete(int itemId);
}

package io.extact.msa.rms.application.external.stub;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;

import io.extact.msa.rms.application.external.dto.AddRentalItemDto;
import io.extact.msa.rms.application.external.dto.RentalItemDto;
import io.extact.msa.rms.application.external.restclient.RentalItemApiRestClient;
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
    public RentalItemDto get(Integer itemId) {
        return stub.get(itemId)
                .map(this::convertRentalItemDto)
                .orElse(null);
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

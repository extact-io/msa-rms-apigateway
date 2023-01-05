package io.extact.msa.rms.apigateway.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import io.extact.msa.rms.apigateway.external.RentalItemApi;
import io.extact.msa.rms.apigateway.external.dto.AddRentalItemDto;
import io.extact.msa.rms.apigateway.external.dto.RentalItemDto;
import io.extact.msa.rms.apigateway.model.RentalItemModel;
import io.extact.msa.rms.apigateway.service.event.DeleteRentalItemEvent;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

@ApplicationScoped
public class RentalItemGwService {

    private RentalItemApi api;
    private Event<DeleteRentalItemEvent> notificator;

    @Inject
    public RentalItemGwService(RentalItemApi api, Event<DeleteRentalItemEvent> notificator) {
        this.api = api;
        this.notificator = notificator;
    }

    public List<RentalItemModel> getAll() {
        return api.getAll().stream()
                .map(RentalItemDto::toModel)
                .toList();
    }

    public RentalItemModel add(RentalItemModel addModel) throws BusinessFlowException {
        return api.add(addModel.transform(AddRentalItemDto::from)).toModel();
    }

    public RentalItemModel update(RentalItemModel updateModel) {
        return api.update(updateModel.transform(RentalItemDto::from)).toModel();
    }

    public void delete(int deleteId) throws BusinessFlowException {
        notificator.fire(new DeleteRentalItemEvent(deleteId));
        api.delete(deleteId);
    }
}

package io.extact.msa.rms.application.service;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import io.extact.msa.rms.application.external.UserAccountApi;
import io.extact.msa.rms.application.external.dto.AddUserAccountDto;
import io.extact.msa.rms.application.external.dto.UserAccountDto;
import io.extact.msa.rms.application.model.UserAccountModel;
import io.extact.msa.rms.application.service.event.DeleteUserAccountEvent;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

@ApplicationScoped
public class UserAccountAppService {

    private UserAccountApi api;
    private Event<DeleteUserAccountEvent> notificator;

    @Inject
    public UserAccountAppService(UserAccountApi api, Event<DeleteUserAccountEvent> notificator) {
        this.api = api;
        this.notificator = notificator;
    }

    public List<UserAccountModel> getAll() {
        return api.getAll().stream()
                .map(UserAccountDto::toModel)
                .toList();
    }

    public Optional<UserAccountModel> get(int id) {
        return api.get(id).map(UserAccountDto::toModel);
    }

    public UserAccountModel add(UserAccountModel addModel) throws BusinessFlowException {
        return api.add(addModel.transform(AddUserAccountDto::from)).toModel();
    }

    public UserAccountModel update(UserAccountModel updateModel) {
        return api.update(updateModel.transform(UserAccountDto::from)).toModel();
    }

    public void delete(int deleteId) throws BusinessFlowException {
        notificator.fire(new DeleteUserAccountEvent(deleteId));
        api.delete(deleteId);
    }

    public UserAccountModel authenticate(String loginId, String password) {
        return api.authenticate(loginId, password).toModel();
    }
}

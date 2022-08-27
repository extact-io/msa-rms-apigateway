package io.extact.msa.rms.application.external.bridge;

import static io.extact.msa.rms.application.external.ApiType.*;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.extact.msa.rms.application.external.UserAccountApi;
import io.extact.msa.rms.application.external.dto.AddUserAccountDto;
import io.extact.msa.rms.application.external.dto.UserAccountDto;
import io.extact.msa.rms.application.external.restclient.UserAccountApiRestClient;
import io.extact.msa.rms.platform.core.extension.EnabledIfRuntimeConfig;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = PROP_NAME, value = REAL)
public class UserAccountApiRestBridge implements UserAccountApi {

    private UserAccountApiRestClient client;

    @Inject
    public UserAccountApiRestBridge(@RestClient UserAccountApiRestClient client) {
        this.client = client;
    }

    @Override
    public List<UserAccountDto> getAll() {
        return client.getAll();
    }

    @Override
    public Optional<UserAccountDto> get(int userAccountId) {
        return Optional.ofNullable(client.get(userAccountId));
    }

    @Override
    public UserAccountDto add(AddUserAccountDto dto) throws BusinessFlowException {
        return client.add(dto);
    }

    @Override
    public UserAccountDto update(UserAccountDto dto) {
        return client.update(dto);
    }

    @Override
    public void delete(int userAccountId) throws BusinessFlowException {
        client.delete(userAccountId);
    }

    @Override
    public UserAccountDto authenticate(String loginId, String password) throws BusinessFlowException {
        return client.authenticate(loginId, password);
    }
}

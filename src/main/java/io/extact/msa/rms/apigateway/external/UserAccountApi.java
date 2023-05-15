package io.extact.msa.rms.apigateway.external;

import java.util.List;
import java.util.Optional;

import io.extact.msa.rms.apigateway.external.dto.AddUserAccountDto;
import io.extact.msa.rms.apigateway.external.dto.UserAccountDto;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;

public interface UserAccountApi {

    List<UserAccountDto> getAll();

    UserAccountDto get(int userAccountId);

    default Optional<UserAccountDto> getOptional(int userAccountId) {
        return Optional.ofNullable(this.get(userAccountId));
    }

    UserAccountDto add(AddUserAccountDto dto) throws BusinessFlowException;

    UserAccountDto update(UserAccountDto dto);

    void delete(int userAccountId) throws BusinessFlowException;

    UserAccountDto authenticate(String loginId, String password) throws BusinessFlowException;
}

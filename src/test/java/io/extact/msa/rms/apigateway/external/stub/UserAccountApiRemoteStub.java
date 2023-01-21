package io.extact.msa.rms.apigateway.external.stub;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.apigateway.external.dto.AddUserAccountDto;
import io.extact.msa.rms.apigateway.external.dto.UserAccountDto;
import io.extact.msa.rms.apigateway.external.restclient.UserAccountApiRestClient;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.test.stub.UserAccountMemoryStub;
import io.extact.msa.rms.platform.test.stub.dto.AddUserAccountStubDto;
import io.extact.msa.rms.platform.test.stub.dto.UserAccountStubDto;

@ApplicationScoped
@Path("users")
public class UserAccountApiRemoteStub implements UserAccountApiRestClient {

    private UserAccountMemoryStub stub = new UserAccountMemoryStub();

    @PostConstruct
    public void init() {
        stub.init();
    }

    @Override
    public List<UserAccountDto> getAll() {
        return stub.getAll().stream()
                .map(this::convertUserAccountDto)
                .toList();
    }

    @Override
    public UserAccountDto get(Integer userId) {
        return stub.get(userId)
                .map(this::convertUserAccountDto)
                .orElse(null);
    }

    @Override
    public UserAccountDto add(AddUserAccountDto dto) throws BusinessFlowException {
        return stub.add(convertAddUserAccountDto(dto))
                .transform(this::convertUserAccountDto);
    }

    @Override
    public UserAccountDto update(UserAccountDto dto) {
        return stub.update(convertUserAccountStubDto(dto))
                .transform(this::convertUserAccountDto);
    }

    @Override
    public void delete(Integer userId) throws BusinessFlowException {
        stub.delete(userId);
    }

    @Override
    public UserAccountDto authenticate(String loginId, String password) {
        return stub.authenticate(loginId, password)
                .transform(this::convertUserAccountDto);
    }

    // ----------------------------------------------------- convert methods

    private UserAccountDto convertUserAccountDto(UserAccountStubDto src) {
        return UserAccountDto.of(src.getId(), src.getLoginId(), src.getPassword(), src.getUserName(),
                src.getPhoneNumber(), src.getContact(), UserType.valueOf(src.getUserType()));
    }
    private UserAccountStubDto convertUserAccountStubDto(UserAccountDto src) {
        return UserAccountStubDto.of(src.getId(), src.getLoginId(), src.getPassword(), src.getUserName(),
                src.getPhoneNumber(), src.getContact(), UserType.valueOf(src.getUserType()));
    }
    private AddUserAccountStubDto convertAddUserAccountDto(AddUserAccountDto src) {
        return AddUserAccountStubDto.of(src.getLoginId(), src.getPassword(), src.getUserName(),
                src.getPhoneNumber(), src.getContact(), src.getUserType());
    }
}

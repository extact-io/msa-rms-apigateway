package io.extact.msa.rms.apigateway.webapi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import io.extact.msa.rms.apigateway.service.UserAccountGwService;
import io.extact.msa.rms.apigateway.webapi.dto.LoginEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.rms.platform.core.validate.ValidateParam;

@Path("login")
@ValidateParam
@ApplicationScoped
public class LoginResourceImpl implements LoginResource {

    private UserAccountGwService userService;

    @Inject
    public LoginResourceImpl(UserAccountGwService userService) {
        this.userService = userService;
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(String loginId, String password) {
        return authenticate(LoginEventDto.of(loginId, password)); // this method is for debug so convert.
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(LoginEventDto loginDto) {
        return userService.authenticate(loginDto.getLoginId(), loginDto.getPassword())
                .transform(UserAccountResourceDto::from);
    }
}

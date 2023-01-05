package io.extact.msa.rms.apigateway.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.apigateway.model.UserAccountModel;
import io.extact.msa.rms.platform.fw.domain.constraint.ItemName;
import io.extact.msa.rms.platform.fw.domain.constraint.LoginId;
import io.extact.msa.rms.platform.fw.domain.constraint.Passowrd;
import io.extact.msa.rms.platform.fw.domain.constraint.UserName;
import io.extact.msa.rms.platform.fw.domain.constraint.UserTypeConstraint;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import lombok.Getter;
import lombok.Setter;


@Schema(description = "ユーザ登録用DTO")
@Getter @Setter
public class AddUserAccountEventDto {

    @Schema(required = true)
    @LoginId
    private String loginId;

    @Schema(required = true)
    @Passowrd
    private String password;

    @Schema(required = true)
    @UserName
    private String userName;

    @Schema(required = false)
    @ItemName
    private String phoneNumber;

    @Schema(required = false)
    private String contact;

    @Schema(required = true)
    @UserTypeConstraint
    private UserType userType;

    public UserAccountModel toModel() {
        return UserAccountModel.ofTransient(loginId, password, userName, phoneNumber, contact, userType);
    }
}

package io.extact.msa.rms.apigateway.webapi.dto;

import java.util.Set;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.apigateway.model.UserAccountModel;
import io.extact.msa.rms.platform.core.jwt.provider.UserClaims;
import io.extact.msa.rms.platform.fw.domain.constraint.Contact;
import io.extact.msa.rms.platform.fw.domain.constraint.LoginId;
import io.extact.msa.rms.platform.fw.domain.constraint.Passowrd;
import io.extact.msa.rms.platform.fw.domain.constraint.PhoneNumber;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.UserName;
import io.extact.msa.rms.platform.fw.domain.constraint.UserTypeConstraint;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "ユーザDTO")
@Getter @Setter @ToString
public class UserAccountResourceDto implements UserClaims {

    @Schema(required = true)
    @RmsId
    private Integer id;

    @Schema(required = true)
    @LoginId
    private String loginId;

    @Schema(required = true)
    @Passowrd
    private String password;

    @Schema(required = false)
    @UserName
    private String userName;

    @Schema(required = false)
    @PhoneNumber
    private String phoneNumber;

    @Schema(required = false)
    @Contact
    private String contact;

    @Schema(required = true)
    @UserTypeConstraint
    private UserType userType;

    public static UserAccountResourceDto from(UserAccountModel model) {
        if (model == null) {
            return null;
        }
        var dto = new UserAccountResourceDto();
        dto.setId(model.getId());
        dto.setLoginId(model.getLoginId());
        dto.setPassword(model.getPassword());
        dto.setUserName(model.getUserName());
        dto.setPhoneNumber(model.getPhoneNumber());
        dto.setContact(model.getContact());
        dto.setUserType(model.getUserType().name());
        return dto;
    }

    public UserAccountModel toModel() {
        return UserAccountModel.of(id, loginId, password, userName, phoneNumber, contact, userType);
    }

    // original getter
    public String getUserType() {
        return userType.name();
    }

    // original setter
    public void setUserType(String userType) {
        this.userType = UserType.valueOf(userType);
    }


    // --------------------------------------------- implements UserClaims

    @Override
    public String getUserId() {
        return String.valueOf(getId());
    }
    @Override
    public String getUserPrincipalName() {
        return getContact() + "@rms.com";
    }
    @Override
    public Set<String> getGroups() {
        return Set.of(getUserType());
    }
}

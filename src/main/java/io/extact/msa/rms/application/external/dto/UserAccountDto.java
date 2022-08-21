package io.extact.msa.rms.application.external.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.application.model.UserAccountModel;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class UserAccountDto {

    private Integer id;
    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private UserType userType;

    public static UserAccountDto from(UserAccountModel model) {
        return UserAccountDto.of(model.getId(), model.getLoginId(), model.getPassword(), model.getUserName(),
                model.getPhoneNumber(), model.getContact(), model.getUserType());
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

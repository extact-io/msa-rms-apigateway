package io.extact.msa.rms.apigateway.external.dto;

import io.extact.msa.rms.apigateway.model.UserAccountModel;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class AddUserAccountDto {

    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private UserType userType;

    public static AddUserAccountDto from(UserAccountModel model) {
        if (model == null) {
            return null;
        }
        return AddUserAccountDto.of(model.getLoginId(), model.getPassword(), model.getUserName(),
                model.getPhoneNumber(), model.getContact(), model.getUserType());
    }
}

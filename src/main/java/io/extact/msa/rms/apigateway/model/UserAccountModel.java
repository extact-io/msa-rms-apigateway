package io.extact.msa.rms.apigateway.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class UserAccountModel implements IdProperty, Transformable {

    private Integer id;
    private String loginId;
    private String password;
    private String userName;
    private String phoneNumber;
    private String contact;
    private UserType userType;


    // ----------------------------------------------------- factory methods

    public static UserAccountModel ofTransient(String loginId, String password, String userName, String phoneNumber, String contact, UserType userType) {
        return of(null, loginId, password, userName, phoneNumber, contact, userType);
    }

    // ----------------------------------------------------- service methods

    public boolean isAdmin() {
        return this.userType == UserType.ADMIN;
    }

    public void setAdmin(boolean isAdmin) {
        this.userType = isAdmin ? UserType.ADMIN : UserType.MEMBER;
    }

    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

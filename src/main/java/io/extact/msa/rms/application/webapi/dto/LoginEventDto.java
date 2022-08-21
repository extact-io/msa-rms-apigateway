package io.extact.msa.rms.application.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.platform.fw.domain.constraint.LoginId;
import io.extact.msa.rms.platform.fw.domain.constraint.Passowrd;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ログインDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LoginEventDto {

    @Schema(required = true, minLength = 5, maxLength = 10)
    @LoginId
    private String loginId;

    @Schema(required = true, minLength = 5, maxLength = 10)
    @Passowrd
    private String password;
}

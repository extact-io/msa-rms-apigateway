package io.extact.msa.rms.application.webapi;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import io.extact.msa.rms.platform.core.jwt.JwtSecurityFilterFeature;
import io.extact.msa.rms.platform.core.jwt.login.LoginUserRequestFilter;
import io.extact.msa.rms.platform.core.role.RoleSecurityDynamicFeature;
import io.extact.msa.rms.platform.fw.webapi.GenericErrorInfo;
import io.extact.msa.rms.platform.fw.webapi.ValidationErrorInfoImpl;
import io.extact.msa.rms.platform.fw.webapi.server.ManagementResource;
import io.extact.msa.rms.platform.fw.webapi.server.RmsApplication;

/**
 * RESTアプリケーションのコンフィグ情報。
 * REST API全体に関することをOpenAPIのアノテーションで定義している
 */
@SecurityScheme(
        securitySchemeName = "RmsJwtAuth",
        description = "認証と認可はMicroProfile JWT RBAC Securityの仕様をもとに行い認証エラーの場合は401を認可エラーの場合は403を返す",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
        info = @Info(title = "レンタル予約システムの公開API", version = "1.0.1-SNAPSHOT",
            contact = @Contact(name = "課外活動", url = "https://extact-io.github.io/rms-website/")),
        components = @Components(
                schemas = {
                    @Schema(
                            name = "localDateTime",
                            description = "日時型",
                            format = "yyyyMMdd HH:mm",
                            example = "20210314 09:00",
                            implementation = LocalDateTime.class
                            )
                },
                responses = {
                    @APIResponse(
                            name = "NoContent",
                            responseCode = "204",
                            description = "該当データなしの場合。1件取得で戻り値にnullを返すことが妥当な場合は正常のため404ではなくボディなしの204を返す"
                            ),
                    @APIResponse(
                            name = "NotFound",
                            responseCode = "404",
                            description = "該当データがない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "ServerError",
                            responseCode = "500",
                            description = "アプリケーション内部でエラーが発生した場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のRmsSystemExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "Forbidden",
                            responseCode = "403",
                            description = "対象データに対する操作権限がない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "UnknownData",
                            responseCode = "404",
                            description = "処理対象データが存在しない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "DataDupricate",
                            responseCode = "409",
                            description = "登録データが既に登録されている",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "DataRefered",
                            responseCode = "409",
                            description = "操作対象を参照するデータが存在する",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "ParameterError",
                            responseCode = "400",
                            description = "パラメータエラーの場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のConstraintViolationExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorInfoImpl.class))
                            )
                })
        )
@ApplicationScoped
public class ApplicationConfig extends RmsApplication {

    @Override
    protected Set<Class<?>> getWebApiClasses() {
        return Set.of(
                JwtSecurityFilterFeature.class,
                RoleSecurityDynamicFeature.class,
                LoginUserRequestFilter.class,
                ManagementResource.class,
                ApplicationResourceImpl.class
                );
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
                    // The following keys are defined in `ServerProperties.BV_SEND_ERROR_IN_RESPONSE`
                    "jersey.config.beanValidation.disable.server", true  // jerseyのJAX-RSのBeanValidationサポートをOFFにする
                );
    }
}

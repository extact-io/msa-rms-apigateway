package io.extact.msa.rms.apigateway.webapi;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.extact.msa.rms.apigateway.webapi.dto.LoginEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.fw.domain.constraint.LoginId;
import io.extact.msa.rms.platform.fw.domain.constraint.Passowrd;

public interface LoginResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Tag(name = "Authenticate")
    @Operation(operationId = "authenticateForTest", summary = "ユーザ認証を行う（curlのテスト用）", description = "ログイン名とパスワードに一致するユーザを取得する")
    @Parameter(name = "loginId", description = "ログインId", required = true, schema = @Schema(implementation = String.class, minLength = 5, maxLength = 10))
    @Parameter(name = "password", description = "パスワード", required = true, schema = @Schema(implementation = String.class, minLength = 5, maxLength = 10))
    @APIResponse(responseCode = "200", description = "認証成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
       UserAccountResourceDto authenticate(@LoginId @QueryParam("loginId") String loginId,
            @Passowrd @QueryParam("password") String password);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Tag(name = "Authenticate")
    @Operation(operationId = "authenticate", summary = "ユーザ認証を行う", description = "ログイン名とパスワードに一致するユーザを取得する")
    @Parameter(name = "loginDto", description = "ログインIDとパスワード", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginEventDto.class)))
    @APIResponse(responseCode = "200", description = "認証成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    UserAccountResourceDto authenticate(@Valid LoginEventDto loginDto);
}

package io.extact.msa.rms.apigateway.webapi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.extact.msa.rms.apigateway.webapi.dto.AddRentalItemEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.AddUserAccountEventDto;
import io.extact.msa.rms.apigateway.webapi.dto.RentalItemResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.ReservationResourceDto;
import io.extact.msa.rms.apigateway.webapi.dto.UserAccountResourceDto;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;

/**
 * レンタル予約システムのREST APIインタフェース。
 * MicroProfileのOpenAPIのアノテーションを使いAPIの詳細情報を付加している。<br>
 * 全体に関するAPI情報は{@link ApiGatewayApplication}に定義。
 */
public interface ApiGatewayResource {

    /** for @RolesAllowed const */
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String MEMBER_ROLE = "MEMBER";

    @GET
    @Path("/reservations/item/{itemId}/startdate/{startDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "findReservationByRentalItemAndStartDate", summary = "指定されたレンタル品と利用開始日で予約を検索する", description = "指定されたレンタル品と利用開始日に一致する予約を検索する")
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @Parameter(name = "startDate", description = "利用開始日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<ReservationResourceDto> findReservationByRentalItemAndStartDate(@RmsId @PathParam("itemId") Integer itemId,
            @NotNull @PathParam("startDate") LocalDate startDate);

    @GET
    @Path("/reservations/reserver/{reserverId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "findReservationByReserverId", summary = "指定されたユーザが予約者の予約を検索する", description = "指定されたユーザが予約者の予約を検索する")
    @Parameter(name = "reserverId", description = "ユーザID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<ReservationResourceDto> findReservationByReserverId(@RmsId @PathParam("reserverId") Integer reserverId);

    @GET
    @Path("/reservations/own")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "getOwnReservations", summary = "自分の予約一覧を取得する", description = "ログインユーザが予約者となっている予約の一覧を取得する。このAPIは/reservations/reserver/{reserverId}のエイリアスとなっている")
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<ReservationResourceDto> getOwnReservations();

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    //--- for OpenAPI
    @Tag(name = "Member")
    @Tag(name = "Admin")
    @Operation(operationId = "getAllRentalItems", summary = "レンタル品の全件を取得する", description = "登録されているすべてのレンタル品を取得する")
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<RentalItemResourceDto> getAllRentalItems();

    @POST
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "addReservation", summary = "レンタル品を予約する", description = "予約対象のレンタル品が存在しない場合は404を予定期間に別の予約が既に入っている場合は409を返す")
    @Parameter(name = "dto", description = "登録内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddReservationEventDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    ReservationResourceDto addReservation(@Valid AddReservationEventDto dto);

    @DELETE
    @Path("/reservations/own/{reservationId}")
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "cancelReservation", summary = "予約をキャンセルする", description = "依頼された予約IDに対する予約をキャンセルする。予約のキャンセルは予約した人しか行えない。"
            + "他の人が予約キャンセルを行った場合は禁止操作としてエラーにする")
    @Parameter(name = "reservationId", description = "予約ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    void cancelReservation(@RmsId @PathParam("reservationId") Integer reservationId);

    @GET
    @Path("/reservations/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "findReservationByRentalItemId", summary = "指定されたレンタル品に対する予約を検索する", description = "指定されたレンタル品に対する予約を検索する")
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<ReservationResourceDto> findReservationByRentalItemId(@RmsId @PathParam("itemId") Integer itemId);

    @GET
    @Path("/items/rentable")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "findCanRentedItemAtTerm", summary = "該当期間に予約可能なレンタル品を検索する", description = "該当期間に予約可能なレンタル品を検索する")
    @Parameter(name = "from", description = "利用開始日時", in = ParameterIn.QUERY, required = true, schema = @Schema(ref = "#/components/schemas/LocalDateTime"))
    @Parameter(name = "to", description = "利用開始日時", in = ParameterIn.QUERY, required = true, schema = @Schema(ref = "#/components/schemas/LocalDateTime"))
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<RentalItemResourceDto> findCanRentedItemAtTerm(@NotNull @QueryParam("from") LocalDateTime from, @NotNull @QueryParam("to") LocalDateTime to);

    @GET
    @Path("/items/{itemId}/rentable")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(MEMBER_ROLE)
    //--- for OpenAPI
    @Tag(name = "Member")
    @Operation(operationId = "canRentedItemAtTerm", summary = "レンタル品が該当期間に予約可能かを返す", description = "レンタル品が該当期間に予約可能かを返す")
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @Parameter(name = "from", description = "利用開始日時", in = ParameterIn.QUERY, required = true, schema = @Schema(ref = "#/components/schemas/LocalDateTime"))
    @Parameter(name = "to", description = "利用開始日時", in = ParameterIn.QUERY, required = true, schema = @Schema(ref = "#/components/schemas/LocalDateTime"))
    @APIResponse(responseCode = "200", description = "trueならレンタル可", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.BOOLEAN, implementation = Boolean.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    boolean canRentedItemAtTerm(@RmsId @PathParam("itemId") Integer rentalItemId, @NotNull @QueryParam("from") LocalDateTime from,
            @NotNull @QueryParam("to") LocalDateTime to);

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "addRentalItem", summary = "レンタル品を登録する", description = "シリアル番号が既に使われている場合は409を返す")
    @Parameter(name = "dto", description = "登録内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddRentalItemEventDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    RentalItemResourceDto addRentalItem(@Valid AddRentalItemEventDto dto);

    @PUT
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "updateRentalItem", summary = "レンタル品を更新する", description = "依頼されたレンタル品を更新する")
    @Parameter(name = "updateDto", description = "更新内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    RentalItemResourceDto updateRentalItem(@Valid RentalItemResourceDto updateDto);

    @DELETE
    @Path("/items/{itemId}")
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "deleteRentalItem", summary = "レンタル品を削除する", description = "削除対象のレンタル品を参照する予約が存在する場合は削除は行わずエラーにする")
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataRefered")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    void deleteRentalItem(@RmsId @PathParam("itemId") Integer itemId);

    @GET
    @Path("/reservations")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "getAllReservations", summary = "予約の全件を取得する", description = "登録されているすべての予約を取得する")
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<ReservationResourceDto> getAllReservations();

    @PUT
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "updateReservation", summary = "予約を更新する", description = "依頼された予約を更新する。ユーザアカウントとレンタル品のエンティティは更新時に使用していないためIDのみ設定すればよい")
    @Parameter(name = "updateDto", description = "更新内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功。IDに対するユーザアカウントとレンタル品のエンティティは設定されて返される", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    ReservationResourceDto updateReservation(@Valid ReservationResourceDto updateDto);

    @DELETE
    @Path("/reservations/{reservationId}")
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "deleteReservation", summary = "予約を削除する", description = "予約を削除する")
    @Parameter(name = "reservationId", description = "予約ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    void deleteReservation(@RmsId @PathParam("reservationId") Integer reservationId);

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "getAllUserAccounts", summary = "ユーザの全件を取得する", description = "登録されているすべてのユーザを取得する")
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    List<UserAccountResourceDto> getAllUserAccounts();

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "addUserAccount", summary = "ユーザを登録する", description = "ログインIDが既に使われている場合は409を返す")
    @Parameter(name = "dto", description = "登録内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddUserAccountEventDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    UserAccountResourceDto addUserAccount(@Valid AddUserAccountEventDto dto);

    @PUT
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "updateUserAccount", summary = "ユーザを更新する", description = "依頼されたユーザを更新する")
    @Parameter(name = "dto", description = "更新内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    UserAccountResourceDto updateUserAccount(@Valid UserAccountResourceDto dto);

    @DELETE
    @Path("/users/{userAccountId}")
    @RolesAllowed(ADMIN_ROLE)
    //--- for OpenAPI
    @Tag(name = "Admin")
    @Operation(operationId = "deleteUserAccount", summary = "ユーザを削除する", description = "削除対象のユーザを参照する予約が存在する場合は削除は行わずエラーにする")
    @Parameter(name = "userAccountId", description = "ユーザID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataRefered")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    void deleteUserAccount(@RmsId @PathParam("userAccountId") Integer userAccountId);

    @GET
    @Path("/users/own")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    //--- for OpenAPI
    @Tag(name = "Common")
    @Operation(operationId = "getOwnUserProfile", summary = "自分のプロファイル情報を取得する", description = "ログインしているユーザ自身のプロファイル情報を返す")
    @APIResponse(responseCode = "200", description = "プロファイル情報", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    UserAccountResourceDto getOwnUserProfile();

    @PUT
    @Path("/users/own")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ MEMBER_ROLE, ADMIN_ROLE })
    //--- for OpenAPI
    @Tag(name = "Common")
    @Operation(operationId = "updateUserProfile", summary = "自分のプロファイル情報を更新する", description = "自分以外の情報を更新しようとした場合は禁止操作として403を返す")
    @Parameter(name = "dto", description = "更新内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    @APIResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable")
    UserAccountResourceDto updateUserProfile(@Valid UserAccountResourceDto dto);
}

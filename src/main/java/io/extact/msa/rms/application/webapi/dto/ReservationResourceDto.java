package io.extact.msa.rms.application.webapi.dto;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.application.model.ReservationModel;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "予約DTO")
@Getter @Setter @ToString
@BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
public class ReservationResourceDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true)
    @RmsId
    private Integer id;

    @ReserveStartDateTime
    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    private LocalDateTime startDateTime;

    @ReserveEndDateTime
    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    private LocalDateTime endDateTime;

    @Schema(required = false)
    @Note
    private String note;

    @Schema(required = true)
    @RmsId
    private int rentalItemId;

    @Schema(required = true)
    @RmsId
    private int userAccountId;

    @Schema(required = false)
    private UserAccountResourceDto userAccountDto;

    @Schema(required = false)
    private RentalItemResourceDto rentalItemDto;

    public static ReservationResourceDto from(ReservationModel model) {
        if (model == null) {
            return null;
        }
        var dto = new ReservationResourceDto();
        dto.setId(model.getId());
        dto.setStartDateTime(model.getStartDateTime());
        dto.setEndDateTime(model.getEndDateTime());
        dto.setNote(model.getNote());
        dto.setRentalItemId(model.getRentalItemId());
        dto.setUserAccountId(model.getUserAccountId());
        if (model.getRentalItem() != null) {
            dto.setRentalItemDto(model.getRentalItem().transform(RentalItemResourceDto::from));
        }
        if (model.getUserAccount() != null) {
            dto.setUserAccountDto(model.getUserAccount().transform(UserAccountResourceDto::from));
        }
        return dto;
    }

    public ReservationModel toModel() {
        var reservation = ReservationModel.of(id, startDateTime, endDateTime, note, rentalItemId, userAccountId);
        if (rentalItemDto != null) {
            reservation.setRentalItem(rentalItemDto.toModel());
        }
        if (userAccountDto != null) {
            reservation.setUserAccount(userAccountDto.toModel());
        }
        return reservation;
    }
}

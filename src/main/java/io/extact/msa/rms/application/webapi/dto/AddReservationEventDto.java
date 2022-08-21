package io.extact.msa.rms.application.webapi.dto;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.application.model.ReservationModel;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.rms.platform.fw.domain.constraint.Note;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveEndDateTime;
import io.extact.msa.rms.platform.fw.domain.constraint.ReserveStartDateTimeFuture;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.ValidationGroups.Add;
import lombok.Getter;
import lombok.Setter;


@Schema(description = "予約登録用DTO")
@BeforeAfterDateTime
@Getter @Setter
public class AddReservationEventDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveStartDateTimeFuture(groups = Add.class)
    private LocalDateTime startDateTime;

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveEndDateTime
    private LocalDateTime endDateTime;

    @Schema(required = false)
    @Note
    private String note;

    @RmsId
    @Schema(required = true)
    private int rentalItemId;

    @RmsId
    @Schema(required = true)
    private int userAccountId;

    public ReservationModel toModel() {
        return ReservationModel.ofTransient(startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }
}

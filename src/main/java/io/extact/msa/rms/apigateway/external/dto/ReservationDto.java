package io.extact.msa.rms.apigateway.external.dto;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.apigateway.model.ReservationModel;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class ReservationDto {

    private Integer id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;

    public static ReservationDto from(ReservationModel model) {
        return ReservationDto.of(model.getId(), model.getStartDateTime(), model.getEndDateTime(), model.getNote(),
                model.getRentalItemId(), model.getUserAccountId());
    }

    public ReservationModel toModel() {
        return ReservationModel.of(id, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    public ReservationModel toModel(Function<ReservationDto, ReservationModel> composer) {
        return composer.apply(this);
    }

    public DateTimePeriod getReservePeriod() {
        return new DateTimePeriod(startDateTime, endDateTime);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package io.extact.msa.rms.apigateway.external.dto;

import java.time.LocalDateTime;

import io.extact.msa.rms.apigateway.model.ReservationModel;
import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class AddReservationDto {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;

    public static AddReservationDto from(ReservationModel model) {
        if (model == null) {
            return null;
        }
        return AddReservationDto.of(model.getStartDateTime(), model.getEndDateTime(), model.getNote(),
                model.getRentalItemId(), model.getUserAccountId());
    }

    public DateTimePeriod getReservePeriod() {
        return new DateTimePeriod(startDateTime, endDateTime);
    }
}

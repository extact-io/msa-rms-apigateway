package io.extact.msa.rms.apigateway.model;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class ReservationModel implements IdProperty, Transformable {

    private Integer id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;
    private RentalItemModel rentalItem;
    private UserAccountModel userAccount;


    // ----------------------------------------------------- factory methods

    public static ReservationModel of(Integer reservationId, LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            int rentalItemId, int userAccountId) {
        return ReservationModel.of(reservationId, startDateTime, endDateTime, note, rentalItemId, userAccountId, null,
                null);
    }

    public static ReservationModel ofTransient(LocalDateTime startDateTime, LocalDateTime endDateTime, String note, int rentalItemId, int userAccountId) {
        return of(null, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package io.extact.msa.rms.application.external.dto;

import io.extact.msa.rms.application.model.RentalItemModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class AddRentalItemDto {

    private String serialNo;
    private String itemName;

    public static AddRentalItemDto from(RentalItemModel model) {
        if (model == null) {
            return null;
        }
        return AddRentalItemDto.of(model.getSerialNo(), model.getItemName());
    }
}

package io.extact.msa.rms.application.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.application.model.RentalItemModel;
import io.extact.msa.rms.platform.fw.domain.constraint.ItemName;
import io.extact.msa.rms.platform.fw.domain.constraint.SerialNo;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "レンタル品登録用DTO")
@Getter @Setter
public class AddRentalItemEventDto {

    @Schema(required = true)
    @SerialNo
    private String serialNo;

    @Schema(required = false)
    @ItemName
    private String itemName;

    public RentalItemModel toModel() {
        return RentalItemModel.ofTransient(serialNo, itemName);
    }
}

package io.extact.msa.rms.apigateway.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.extact.msa.rms.apigateway.model.RentalItemModel;
import io.extact.msa.rms.platform.fw.domain.constraint.ItemName;
import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.domain.constraint.SerialNo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Schema(description = "レンタル品DTO")
@Getter @Setter @ToString
public class RentalItemResourceDto {

    @Schema(required = true)
    @RmsId
    private Integer id;

    @Schema(required = true)
    @SerialNo
    private String serialNo;

    @Schema(required = false)
    @ItemName
    private String itemName;

    public static RentalItemResourceDto from(RentalItemModel model) {
        if (model == null) {
            return null;
        }
        var dto = new RentalItemResourceDto();
        dto.setId(model.getId());
        dto.setSerialNo(model.getSerialNo());
        dto.setItemName(model.getItemName());
        return dto;
    }

    public RentalItemModel toModel() {
        return RentalItemModel.of(id, serialNo, itemName);
    }
}

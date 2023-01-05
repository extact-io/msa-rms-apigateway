package io.extact.msa.rms.apigateway.external.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.apigateway.model.RentalItemModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class RentalItemDto {

    private Integer id;
    private String serialNo;
    private String itemName;

    public static RentalItemDto from(RentalItemModel model) {
        if (model == null) {
            return null;
        }
        return RentalItemDto.of(model.getId(), model.getSerialNo(), model.getItemName());
    }

    public RentalItemModel toModel() {
        return RentalItemModel.of(id, serialNo, itemName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

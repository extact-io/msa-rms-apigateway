package io.extact.msa.rms.application.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.extact.msa.rms.platform.fw.domain.IdProperty;
import io.extact.msa.rms.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@Getter @Setter
public class RentalItemModel implements IdProperty, Transformable {
    private Integer id;
    private String serialNo;
    private String itemName;

    public static RentalItemModel ofTransient(String serialNo, String itemName) {
        return of(null, serialNo, itemName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

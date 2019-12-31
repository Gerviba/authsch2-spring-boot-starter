package hu.gerviba.authsch2springbootstarter.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BMEUnitScope {
    BME(true, false, false, false),
    BME_VIK(true, true, false, false),
    BME_ACTIVE(true, false, true, false),
    BME_VIK_ACTIVE(true, true, true, false),
    BME_VIK_NEWBIE(true, true, false, true);

    private final boolean bme;
    private final boolean vik;
    private final boolean active;
    private final boolean newbie;
    
}

package hu.gerviba.authsch2springbootstarter.struct;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
public final class ProfileDataResponse implements Serializable {

    private final UUID internalId;
    private final String displayName;
    private final String surname;
    private final String givenName;
    private final String mail;
    private final String neptun;
    private final Map<String, String> linkedAccounts;
    private final List<PersonEntitlement> eduPersonEntitlement;
    private final String roomNumber;
    private final String mobile;
    private final List<String> courses;
    private final List<Entrant> entrants;
    private final List<String> admembership;
    private final List<BMEUnitScope> bmeunitscope;
    private final String permanentAddress;

}

package hu.gerviba.authsch2springbootstarter.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
final class Entrant implements Serializable {

    @NonNull
    private final int groupId;

    @NonNull
    private final String groupName;

    /**
     * Possible values are: KB, AB or ÁB
     */
    @NonNull
    private final String entrantType;

}

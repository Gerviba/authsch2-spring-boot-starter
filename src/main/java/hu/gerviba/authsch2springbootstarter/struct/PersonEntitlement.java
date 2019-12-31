package hu.gerviba.authsch2springbootstarter.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
final class PersonEntitlement implements Serializable {

    @NonNull
    private final long id;

    @NonNull
    private final String name;

    @NonNull
    private final String status;

    @NonNull
    private final List<String> title;

    /**
     * Date Format: YYYY-MM-DD
     */
    @NonNull
    private final String start;

    /**
     * Date Format: YYYY-MM-DD
     * It can be null!
     */
    @Nullable
    private final String end;

}

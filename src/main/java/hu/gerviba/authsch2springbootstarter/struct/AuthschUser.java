package hu.gerviba.authsch2springbootstarter.struct;

import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface AuthschUser {

    @NonNull
    Long getId();

    @NonNull
    UUID getInternalId();

    @NonNull
    default List<String> getRoles() {
        return Collections.emptyList();
    }

}

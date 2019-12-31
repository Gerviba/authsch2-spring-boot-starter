package hu.gerviba.authsch2springbootstarter;

import hu.gerviba.authsch2springbootstarter.struct.AuthschLoginLogicSupplier;
import hu.gerviba.authsch2springbootstarter.struct.AuthschUser;
import hu.gerviba.authsch2springbootstarter.struct.ProfileDataResponse;
import hu.gerviba.authsch2springbootstarter.struct.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Slf4j
@Service
public class AuthschAuthorisationExtractor implements AuthoritiesExtractor, PrincipalExtractor {

    @Autowired
    private AuthschLoginLogicSupplier logic;

    private final ConcurrentHashMap<String, ProfileDataResponse> profileInfos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AuthschUser> entities = new ConcurrentHashMap<>();

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        String internalId = (String) map.get("internal_id");
        if (log.isDebugEnabled())
            log.debug("Extracting user '{}' authority: {}", internalId, map.toString());

        ProfileDataResponse.ProfileDataResponseBuilder response = ProfileDataResponse.builder();
        for (Scope scope : Scope.values())
            if (scope.canApply(map))
                scope.apply(response, map);

        ProfileDataResponse profile = response.build();
        if (log.isDebugEnabled())
            log.debug("User profile: {}", profile.toString());

        profileInfos.put(internalId, profile);
        AuthschUser user = logic.getUserExists().test(profile)
                ? logic.getLoadUser().apply(profile)
                : logic.getCreateUser().apply(profile);
        entities.put(internalId, user);

        return user.getId();
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        String internalId = (String) map.get("internal_id");
        List<String> roles = logic.getResolveRoles().apply(profileInfos.get(internalId), entities.get(internalId));
        if (log.isDebugEnabled())
            log.debug("Roles for user '{}': {}", internalId, roles.toString());

        profileInfos.remove(internalId);
        entities.remove(internalId);

        return roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}

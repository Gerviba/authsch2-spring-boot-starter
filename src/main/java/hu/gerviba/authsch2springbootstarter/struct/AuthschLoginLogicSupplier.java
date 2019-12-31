package hu.gerviba.authsch2springbootstarter.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@Setter
@Accessors(chain = true)
public final class AuthschLoginLogicSupplier {

	@NonNull
	private Class<?> userClass = AuthschUser.class;

	@NonNull
	private List<Scope> requestScopes = new ArrayList<>();

	/**
	 * Used to load user (used inside the injector)
	 */
	@NonNull
	private Function<Long, Object> resolver = id ->
			{ throw new RuntimeException("Procedure to resolve user was not supplied"); };

	/**
     * Used to check if the user was already stored or this is the first time.<br>
	 * <b>Returns</b> true if the uer was found in the database
	 */
	@NonNull
	private Predicate<ProfileDataResponse> userExists = profile -> 
			{ throw new RuntimeException("Procedure to `userExists` was not supplied"); };

	/**
	 * Load user from the database and returns the user entity
	 */
	@NonNull
	private Function<ProfileDataResponse, AuthschUser> loadUser = profile ->
			{ throw new RuntimeException("Procedure to `loadUser` was not supplied"); };

	/**
	 * Creates a new user and returns the user entity
	 */
	@NonNull
	private Function<ProfileDataResponse, AuthschUser> createUser = profile ->
			{ throw new RuntimeException("Procedure to `createUser` was not supplied"); };

	/**
     * Returns the list of the roles of the user
	 */
	@NonNull
	private BiFunction<ProfileDataResponse, AuthschUser, List<String>> resolveRoles =
			(profile, user) -> user.getRoles();

	@NonNull
	public AuthschLoginLogicSupplier setRequestScopes(@NonNull Scope... scopes) {
		this.requestScopes = new ArrayList<>();
		this.requestScopes.addAll(Arrays.asList(scopes));
		
		return this;
	}

}

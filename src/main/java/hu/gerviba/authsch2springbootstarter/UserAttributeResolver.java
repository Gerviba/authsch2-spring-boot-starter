package hu.gerviba.authsch2springbootstarter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.function.Function;

@Slf4j
public class UserAttributeResolver implements HandlerMethodArgumentResolver {

    private final Class<?> customUserClass;
    private final Function<Long, Object> resolver;

    UserAttributeResolver(Class<?> customUserClass, Function<Long, Object> resolver) {
        this.customUserClass = customUserClass;
        this.resolver = resolver;
        log.debug("User attribute injector created for type: " + customUserClass.getSimpleName());
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(customUserClass);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null)
            throw new RuntimeException("Could not retrieve request");

        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null)
            throw new RuntimeException("Cannot inject " + customUserClass
                    + " into endpoint parameter because the user was not authenticated.");

        if (!(userPrincipal instanceof OAuth2Authentication))
            throw new RuntimeException("Invalid user principal type: " + userPrincipal.getClass());

        Object id = ((OAuth2Authentication) userPrincipal).getPrincipal();
        if (!(id instanceof Long))
            throw new RuntimeException("User principal must be a Long");

        return resolver.apply((Long) id);
    }
}

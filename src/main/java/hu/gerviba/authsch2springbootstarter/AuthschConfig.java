package hu.gerviba.authsch2springbootstarter;

import hu.gerviba.authsch2springbootstarter.struct.AuthschLoginLogicSupplier;
import hu.gerviba.authsch2springbootstarter.struct.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableOAuth2Client
@Import(AuthschAuthorisationExtractor.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class AuthschConfig {

    @Autowired
    private AuthschAuthorisationExtractor authorizationExtractor;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private AuthschLoginLogicSupplier logic;

    @Value("${authsch.login-endpoint-name:/login/authsch}")
    String loginEndpointName = "/login/authsch";

    @Value("${authsch.logout-success-url:/}")
    String logoutSuccessUrl = "/";

    public void registerFilters(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .and().logout().logoutSuccessUrl(logoutSuccessUrl).permitAll()
                .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserAttributeResolver(logic.getUserClass(), logic.getResolver()));
    }

    public Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter authschFilter = new OAuth2ClientAuthenticationProcessingFilter(loginEndpointName);
        OAuth2RestTemplate authschTemplate = new OAuth2RestTemplate(authsch(), oauth2ClientContext);

        List<HttpMessageConverter<?>> messageConverters = authschTemplate.getMessageConverters();
        for (HttpMessageConverter<?> x : messageConverters) {
            if (x instanceof MappingJackson2HttpMessageConverter) {
                List<MediaType> supportedMediaTypes = new ArrayList<>(x.getSupportedMediaTypes());
                supportedMediaTypes.add(new MediaType("text", "json"));
                supportedMediaTypes.add(new MediaType("text", "json", StandardCharsets.UTF_8));
                ((MappingJackson2HttpMessageConverter) x).setSupportedMediaTypes(supportedMediaTypes);
                break;
            }
        }

        authschFilter.setRestTemplate(authschTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                authschResource().getUserInfoUri(),
                authsch().getClientId());

        tokenServices.setRestTemplate(authschTemplate);
        tokenServices.setAuthoritiesExtractor(authorizationExtractor);
        tokenServices.setPrincipalExtractor(authorizationExtractor);
        authschFilter.setTokenServices(tokenServices);

        return authschFilter;
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch.client")
    public AuthorizationCodeResourceDetails authsch() {
        AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        client.setScope(Collections.singletonList(logic
                .getRequestScopes()
                .stream()
                .map(Scope::getScope)
                .collect(Collectors.joining("+"))));

        client.setTokenName("access_token");
        client.setAuthenticationScheme(AuthenticationScheme.query);
        client.setClientAuthenticationScheme(AuthenticationScheme.form);
        client.setAccessTokenUri("https://auth.sch.bme.hu/oauth2/token");
        client.setUserAuthorizationUri("https://auth.sch.bme.hu/site/login");

        return client;
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch.resource")
    public ResourceServerProperties authschResource() {
        ResourceServerProperties resource = new ResourceServerProperties();
        resource.setUserInfoUri("https://auth.sch.bme.hu/api/profile/");
        return resource;
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}

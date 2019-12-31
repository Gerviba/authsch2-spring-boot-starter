AuthSCH spring-boot starter
===

## How to import

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

and 

```xml
<dependency>
    <groupId>com.github.gerviba</groupId>
    <artifactId>authsch2-spring-boot-starter</artifactId>
    <version>2.0.1</version>
</dependency>
```

### Gradle

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

and

```groovy
dependencies {
    implementation 'com.github.gerviba:authsch2-spring-boot-starter:2.0.1'
}
```

## How to initialize

- Make sure to use the **latest** version. (See: releases menu)
- Set the `authsch.client.clientSecret` (80 chars) and the `authsch.client.clientId` (20 digit) in your application.properties (or application-production.properties). Note that you'll to create a authsch new project. See [https://auth.sch.bme.hu/](https://auth.sch.bme.hu/)
- Create a bean with type: `AuthschLoginLogicSupplier` 
- Enable the filter and register the resolver

#### Default properties


```properties
## CONFIG VALUES FROM AUTHSCH
authsch.client.clientId=12345678901234567890
authsch.client.clientSecret=1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz12345678

## OPTIONAL PROPERTIES
authsch.login-endpoint-name=/login/authsch
authsch.logout-success-url=/
```

#### Create AuthschLoginLogicSupplier bean

```java

    // Inside a @Configuration class

    @Bean
    public AuthschLoginLogicSupplier authschLogicConfig() {
        return new AuthschLoginLogicSupplier()
                // Your custom user entity class
                .setUserClass(DemoUser.class)
                
                // Requested scopes
                .setRequestScopes(Scope.BASIC, Scope.DISPLAY_NAME, Scope.SURNAME,
                        Scope.GIVEN_NAME, Scope.EMAIL, Scope.LINKED_ACCOUNTS,
                        Scope.EDU_PERSON_ENTILEMENT, Scope.MOBILE,
                        Scope.COURSES, Scope.ENTRANTS, Scope.ACTIVE_DIRECTORY_MEMBERSHIP, 
                        Scope.BME_UNIT_SCOPE, Scope.PERMANENT_ADDRESS)
                        
                // Methods to load, create and authorize user
                .setResolver(users::getUserById)
                .setCreateUser(users::createUser)
                .setLoadUser(users::loadUser)
                .setUserExists(users::isUserExists)
                .setResolveRoles(users::resolveRoles);
    }

```

#### Enable SSO filter

```java

    // Inside a @Configuration class that extends WebSecurityConfigurerAdapter and implements WebMvcConfigurer
    
    @Autowired
    private AuthschConfig authschConfig;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authschConfig.registerFilters(http);
        
        // Additional configuration: ...
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        authschConfig.addArgumentResolvers(argumentResolvers);
    }
    
```

#### Scopes and other

Read more on [https://git.sch.bme.hu/kszk/authsch/wikis/api](https://git.sch.bme.hu/kszk/authsch/wikis/api)
This sample project is to learn how to implement token authentication using Spring Security and Spring Session

# User
* username: user
* password: password
* role: ROLE_ADMIN

# Role Hierarchy
* ROLE_ADMIN > ROLE_USER
* This means that a user who has 'ROLE_ADMIN' can access to the APIs restricted to users who have 'ROLE_USER' as a role as well

# Spring Security Configure
```
@SpringBootApplication
public class SpringSecurityTokenAuthDemoApplication extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
				.authorizeRequests()
					.expressionHandler(webExpressionHandler())
					.antMatchers("/login").permitAll()
					.antMatchers("/user").hasAuthority("ROLE_USER")
					.antMatchers("/admin").hasAuthority("ROLE_ADMIN")
					.anyRequest().authenticated()
				.and()
					.logout().logoutSuccessHandler(((request, response, authentication) -> {
					// do nothing
		}));
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return (username) -> userRepository
				.findByUsername(username)
				.map(a -> new User(a.getUsername(), a.getPassword(), AuthorityUtils.createAuthorityList("ROLE_ADMIN", "write")))
				.orElseThrow(() -> new UsernameNotFoundException("could  not find the user '" + username + "'"));
	}

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		return new HeaderHttpSessionStrategy();
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
		return roleHierarchy;
	}

	private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
		DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
		return defaultWebSecurityExpressionHandler;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityTokenAuthDemoApplication.class, args);
	}
}

```

# Test
[httpie](https://httpie.org) to be used for testing  
"/user" restricted to users who have 'ROLE_USER' as a role  
"/admin" restrided to users who have 'ROLE_ADMIN' as a role  

```
jadeim$ http post localhost:8080/login "username=user" "password=password"
HTTP/1.1 200 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Mon, 27 Feb 2017 06:09:34 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Application-Context: application
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
x-auth-token: 5a86c104-5925-4790-a13f-42675223f075

{
    "authorities": [
        {
            "authority": "ROLE_ADMIN"
        },
        {
            "authority": "write"
        }
    ],
    "token": "5a86c104-5925-4790-a13f-42675223f075",
    "username": "user"
}
```
```
jadeim$ http post localhost:8080/admin "x-auth-token:5a86c104-5925-4790-a13f-42675223f075"
HTTP/1.1 200 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Mon, 27 Feb 2017 06:09:52 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Application-Context: application
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "hello": "admin"
}
```
```
jadeim$ http post localhost:8080/user "x-auth-token:5a86c104-5925-4790-a13f-42675223f075"
HTTP/1.1 200 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Mon, 27 Feb 2017 06:10:02 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Application-Context: application
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "hello": "user"
}
```
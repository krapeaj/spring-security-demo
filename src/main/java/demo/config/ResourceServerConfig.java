package demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/*
    이 아이는 어플리케이션의 resource api의 인증을 처리해주는 아이다.
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "resource_id";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // resource id를 준다.
        resources.resourceId(RESOURCE_ID).stateless(false); // Oauth token 으로만 인증하려면 true를 준다.
    }

    @Override
    public void configure(HttpSecurity http) throws Exception { // 실제 인증 관련 처리를 하는 곳.
        http
                .anonymous().disable()
                .authorizeRequests().antMatchers("/users/**").authenticated() // users 에 접근하는 모든 요청은 다 인증을 필요하도록 설정.
                .and()
                .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler()); // 예외 발생 시 해당 handler를 사용하도록 설정.
    }
}

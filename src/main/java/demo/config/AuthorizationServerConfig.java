package demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/*
    이 클래스는 특정 client(어플리케이션 서버)에 맞는 token을 생성하는 역할을 한다.
*/

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    // 얘네들은 (특히 secret) 외부에 노출되면 안되기 때문에 실제 배포되거나 깃헙에 공유할 때 노출되지 않도록 다른 곳에 (.properties 같은) 관리해서 참조로 쓰는게 좋다.
    private static final String CLIENT_ID = "krapeaj-client";
    private static final String CLIENT_SECRET = "krapeaj-secret";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String IMPLICIT = "implicit";
    private static final String SCOPE_READ = "read";
    private static final String SCOPE_WRITE = "write";
    private static final String TRUST = "trust";
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;

    @Autowired
    private TokenStore tokenStore; // 얘도 in-memory가 아닌 DB를 사용하는 tokenstore로 바꿔주는게 좋다.

    @Autowired
    private AuthenticationManager authenticationManager; //configure()에 있는 정보들(clientid, secret)을 받아서 실제 인증을 처리하는 애.

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        configurer
                .inMemory()
                //access token을 받으려면 여기에 등록되어 있는 클라이언트로 인증이 되어야만 oauth token을 받을 수 있다.
                .withClient(CLIENT_ID) //어플리케이션 이름
                .secret(passwordEncoder.encode(CLIENT_SECRET)) //secret key - facebook, google 인증서버에서 생성해서 줌
                .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
                .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager);
    }
}

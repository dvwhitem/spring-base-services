package com.home.services.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * Created by vitaliy on 7/28/16.
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager auth;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)
            throws Exception {
        security.passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .authenticationManager(auth).tokenStore(tokenStore())
                .approvalStoreDisabled();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.jdbc(dataSource)
                .passwordEncoder(passwordEncoder())
                .withClient("my-trusted-client")
                .authorizedGrantTypes("password", "authorization_code",
                        "refresh_token", "implicit")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes("read", "write", "trust")
                .resourceIds("oauth2-resource").secret("secret")
                .accessTokenValiditySeconds(60).and()
                .withClient("my-client-with-registered-redirect")
                .authorizedGrantTypes("authorization_code")
                .authorities("ROLE_CLIENT").scopes("read", "trust")
                .resourceIds("oauth2-resource")
                .redirectUris("http://anywhere?key=value").and()
                .withClient("my-client-with-secret")
                .authorizedGrantTypes("client_credentials", "password")
                .authorities("ROLE_CLIENT", "ROLE_ADMIN").scopes("read", "write")
                .resourceIds("oauth2-resource").secret("secret");
    }
}

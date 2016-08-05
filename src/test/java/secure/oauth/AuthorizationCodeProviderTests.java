package secure.oauth;

import com.home.services.SpringBaseServicesApplication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.*;

/**
 * Created by vitaliy on 04/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringBaseServicesApplication.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/clean_up_oauth_data.sql")
public class AuthorizationCodeProviderTests {

    public AuthorizationCodeProviderTests() {
        providerTokenServices.setTokenStore(new InMemoryTokenStore());
    }

    @Value("${local.server.port}")
    private int port;

    private AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);

    private AuthorizationCodeServices authorizationCodeServices = new InMemoryAuthorizationCodeServices();

    private DefaultTokenServices providerTokenServices = new DefaultTokenServices();

    private BaseClientDetails client = new BaseClientDetails("my-client-with-registered-redirect", "oauth2-resource", "read", "authorization_code",
            "ROLE_CLIENT");

    private ClientDetailsService clientDetailsService = (ClientDetails)-> client;

    private OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);

    private Map<String, String> parameters = new HashMap<>();


    @Before
    public void init() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo@localhosts", "demo", AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_ADMIN")));
    }

    @After
    public void close() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testGetPageHelloAuthorizationCode() throws Exception {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId("my-client-with-registered-redirect");
        details.setAccessTokenUri("http://localhost:" + this.port + "/oauth/token");
        details.setUserAuthorizationUri("http://localhost:" + this.port + "/oauth/authorize");
        details.setScope(Arrays.asList("read", "trust"));
        OAuth2RestTemplate rest = new OAuth2RestTemplate(details);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = rest.getForEntity("http://localhost:" + this.port + "/hello", String.class);
            Assert.fail("Wrong required redirect users approval");
        } catch (UserRedirectRequiredException e) {
            Assert.assertEquals("A redirect is required to get the users approval", e.getMessage());
        }

        Assert.assertNull(responseEntity);
    }


    @Test
    public void testAuthorizationCodeGrant() {

        Authentication userAuthentication =
                authenticationManager.authenticate(SecurityContextHolder.getContext().getAuthentication());

        parameters.clear();
        parameters.put(OAuth2Utils.CLIENT_ID, "my-client-with-registered-redirect");
        parameters.put(OAuth2Utils.SCOPE, "read");
        OAuth2Request storedOAuth2Request =
                new OAuth2Request(parameters,
                        "my-client-with-registered-redirect",
                        null,
                        true,
                        null,
                        null,
                        null,
                        null,
                        null);
        String code = authorizationCodeServices.createAuthorizationCode(new OAuth2Authentication(
                storedOAuth2Request, userAuthentication));
        parameters.putAll(storedOAuth2Request.getRequestParameters());
        parameters.put("code", code);

        TokenRequest tokenRequest = requestFactory.createTokenRequest(parameters, client);

        AuthorizationCodeTokenGranter granter = new AuthorizationCodeTokenGranter(providerTokenServices,
                authorizationCodeServices, clientDetailsService, requestFactory);

        OAuth2AccessToken token = granter.grant("authorization_code", tokenRequest);
        Assert.assertTrue(providerTokenServices.loadAuthentication(token.getValue()).isAuthenticated());
    }


}

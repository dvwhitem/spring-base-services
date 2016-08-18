package secure.oauth;

import com.home.services.SpringBaseServicesApplication;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;


/**
 * Created by vitaliy on 02/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SpringBaseServicesApplication.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/clean_up_oauth_data.sql")
})
public class ClientCredentialsProviderTests {

    @LocalServerPort
    private int port;

    @Test
    public void testGetPageHelloClientCredentials() throws Exception {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId("my-client-with-secret");
        details.setClientSecret("secret");
        details.setAccessTokenUri("http://localhost:" + this.port + "/oauth/token");
        details.setScope(Arrays.asList("read", "write"));

        OAuth2RestTemplate rest = new OAuth2RestTemplate(details);
        ResponseEntity<String> response = rest.getForEntity("http://localhost:" + this.port + "/hello", String.class);
        Assert.assertEquals("Hello World!", response.getBody());
    }

    @Test
    public void testGetPageHelloInvalidClientCredentials() throws Exception {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId("my-client-with-secret");
        details.setClientSecret("wrong"); // invalid
        details.setAccessTokenUri("http://localhost:" + this.port + "/oauth/token");

        OAuth2RestTemplate rest = new OAuth2RestTemplate(details);
        ResponseEntity<String> response = null;
        try {
            response = rest.getForEntity("http://localhost:" + this.port + "/hello", String.class);
           Assert.fail("Access Denied");
        } catch (OAuth2AccessDeniedException e) {
            Assert.assertEquals(e.getHttpErrorCode(), HttpStatus.FORBIDDEN.value());
        }
        Assert.assertNull(response);
    }
}

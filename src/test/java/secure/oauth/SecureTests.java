package secure.oauth;

import com.home.services.SpringBaseServicesApplication;
import com.home.services.controller.IndexController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by vitaliy on 08/08/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = SpringBaseServicesApplication.class)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/clean_up_oauth_data.sql")
})
public class SecureTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @InjectMocks
    private IndexController indexController;

    private Authentication authentication;

    private MockMvc mockMvc;

    @Before
    public void init() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(indexController).addFilter(springSecurityFilterChain)
                .build();

        AuthenticationManager authenticationManager = this.context
                .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("demo@localhost", "$2a$10$ebyC4Z5WtCXXc.HGDc1Yoe6CLFzcntFmfse6/pTj7CeDY5I05w16C"));
    }

    @After
    public void close() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void testLoginPage() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/anywhere", HttpMethod.GET,
                new HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).contains("_csrf");
    }

    @Test
    public void getSecuredOAuthPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testFailGetUserPageClientCredentials() throws Exception {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId("my-client-with-secret");
        details.setClientSecret("secret");
        details.setAccessTokenUri("http://localhost:" + this.port + "/oauth/token");

        OAuth2RestTemplate rest = new OAuth2RestTemplate(details);
        try {
            rest.getForEntity("http://localhost:" + this.port + "/user", String.class);
            Assert.fail("Access Denied");
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Access is denied");
        }

    }

}

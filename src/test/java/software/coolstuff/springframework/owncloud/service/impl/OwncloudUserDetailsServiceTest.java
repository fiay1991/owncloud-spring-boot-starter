package software.coolstuff.springframework.owncloud.service.impl;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.client.MockRestServiceServer;

import software.coolstuff.springframework.owncloud.model.OwncloudUserDetails;

@RestClientTest(OwncloudUserDetailsService.class)
public class OwncloudUserDetailsServiceTest extends AbstractOwncloudTest {

  @Autowired
  private UserDetailsService userDetailsService;

  private MockRestServiceServer server;

  @Override
  protected String getResourcePrefix() {
    return "/userDetails";
  }

  @Before
  public void setUp() {
    server = MockRestServiceServer.createServer(((OwncloudUserDetailsService) userDetailsService).getRestTemplate());
  }

  @Test
  public void testCorrectClass() {
    Assert.assertEquals(OwncloudUserDetailsService.class, userDetailsService.getClass());
  }

  @Test
  public void testUserDetails_OK() throws IOException {
    server
        .expect(requestToWithPrefix("/users/user1"))
        .andExpect(method(GET))
        .andExpect(header("Authorization", getDefaultBasicAuthorizationHeader()))
        .andRespond(withSuccess(getResponseContentOf("user1_details"), MediaType.TEXT_XML));
    server
        .expect(requestToWithPrefix("/users/user1/groups"))
        .andExpect(method(GET))
        .andExpect(header("Authorization", getDefaultBasicAuthorizationHeader()))
        .andRespond(withSuccess(getResponseContentOf("user1_groups"), MediaType.TEXT_XML));

    UserDetails userDetails = userDetailsService.loadUserByUsername("user1");
    server.verify();

    Assert.assertNotNull(userDetails);

    Assert.assertEquals("user1", userDetails.getUsername());
    Assert.assertNull(userDetails.getPassword());
    checkAuthorities(userDetails.getAuthorities(), "Group1", "Group2");

    Assert.assertTrue(OwncloudUserDetails.class.isAssignableFrom(userDetails.getClass()));
    OwncloudUserDetails owncloudUserDetails = (OwncloudUserDetails) userDetails;
    Assert.assertEquals("Mr. User 1", owncloudUserDetails.getDisplayName());
    Assert.assertEquals("user1@example.com", owncloudUserDetails.getEmail());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testUserDetials_NotFound() throws IOException {
    server
        .expect(requestToWithPrefix("/users/unknown"))
        .andExpect(method(GET))
        .andExpect(header("Authorization", getDefaultBasicAuthorizationHeader()))
        .andRespond(withSuccess(getResponseContentOf("unknown_user"), MediaType.TEXT_XML));

    userDetailsService.loadUserByUsername("unknown");
  }

}

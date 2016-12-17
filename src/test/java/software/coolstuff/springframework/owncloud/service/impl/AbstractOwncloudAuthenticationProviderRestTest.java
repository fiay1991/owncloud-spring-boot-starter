/*
   Copyright (C) 2016 by the original Authors.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/
package software.coolstuff.springframework.owncloud.service.impl;

import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.HttpStatusCodeException;

import software.coolstuff.springframework.owncloud.service.AbstractOwncloudAuthenticationProviderTest;

public abstract class AbstractOwncloudAuthenticationProviderRestTest extends AbstractOwncloudAuthenticationProviderTest
    implements OwncloudServiceRestTest {

  @Autowired
  private OwncloudAuthenticationProvider authenticationProvider;

  @Autowired
  private OwncloudUserDetailsService userDetailsService;

  @Override
  public final OwncloudAuthenticationProvider owncloudService() {
    return authenticationProvider;
  }

  @Override
  protected void prepareTestAuthenticate_OK(Credentials credentials, boolean enabled, String email, String displayName, String... groups) throws IOException {
    respondUser(
        RestRequest.builder()
            .method(GET)
            .url("/cloud/users/" + credentials.getUsername())
            .basicAuthentication(credentials.getBasicAuthorizationHeader())
            .build(),
        enabled,
        email,
        displayName);
    respondGroups(
        RestRequest.builder()
            .server(createServer(userDetailsService))
            .method(GET)
            .url("/cloud/users/" + credentials.getUsername() + "/groups")
            .build(),
        groups);
  }

  @Override
  protected void prepareTestAuthenticate_NOK(Credentials credentials) throws MalformedURLException {
    respondHttpStatus(
        RestRequest.builder()
            .method(GET)
            .url("/cloud/users/" + credentials.getUsername())
            .basicAuthentication(credentials.getBasicAuthorizationHeader())
            .build(),
        HttpStatus.UNAUTHORIZED);
  }

  @Test(expected = HttpStatusCodeException.class)
  public void test404NotFound() throws Exception {
    Credentials credentials = Credentials.builder()
        .username("user1")
        .password("password")
        .build();
    respondHttpStatus(
        RestRequest.builder()
            .method(GET)
            .url("/cloud/users/" + credentials.getUsername())
            .basicAuthentication(credentials.getBasicAuthorizationHeader())
            .build(),
        HttpStatus.NOT_FOUND);
    authenticationProvider.authenticate(credentials.getUsernamePasswordAuthenticationToken());
  }

  @Test(expected = BadCredentialsException.class)
  public void testBadCredentialsByRestError() throws Exception {
    Credentials credentials = Credentials.builder()
        .username("user1")
        .password("password")
        .build();
    respondFailure(
        RestRequest.builder()
            .method(GET)
            .url("/cloud/users/" + credentials.getUsername())
            .basicAuthentication(credentials.getBasicAuthorizationHeader())
            .build(),
        999);
    authenticationProvider.authenticate(credentials.getUsernamePasswordAuthenticationToken());
  }

}

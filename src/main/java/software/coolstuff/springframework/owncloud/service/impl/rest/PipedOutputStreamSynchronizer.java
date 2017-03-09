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
package software.coolstuff.springframework.owncloud.service.impl.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.function.BiFunction;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestOperations;

import software.coolstuff.springframework.owncloud.exception.resource.OwncloudResourceException;
import software.coolstuff.springframework.owncloud.model.OwncloudFileResource;

/**
 * @author mufasa1976
 */
class PipedOutputStreamSynchronizer extends AbstractPipedStreamSynchronizerImpl {

  private final PipedOutputStream pipedOutputStream;

  private PipedOutputStreamSynchronizer(
      final Authentication authentication,
      final OwncloudFileResource owncloudFileResource,
      final OwncloudRestProperties owncloudRestProperties,
      final RestOperations restOperations,
      final BiFunction<URI, String, URI> uriResolver,
      final PipedOutputStream pipedOutputStream) {
    super(authentication, owncloudFileResource, owncloudRestProperties, restOperations, uriResolver);
    this.pipedOutputStream = pipedOutputStream;
  }

  public static PipedStreamSynchronizer.PipedOutputStreamSynchronizerBuilder builder() {
    return new PipedOutputStreamSynchronizerBuilderImpl();
  }

  @Override
  protected HttpMethod getHttpMethod() {
    return HttpMethod.PUT;
  }

  @Override
  protected void createPipedStream() {
    try (InputStream input = new PipedInputStream(pipedOutputStream)) {
      setPipeReady();
      execute(clientHttpRequest -> copy(input, clientHttpRequest.getBody()));
    } catch (IOException e) {
      throw new OwncloudResourceException(e) {
        private static final long serialVersionUID = 5448658359993578985L;
      };
    }
  }

  private static class PipedOutputStreamSynchronizerBuilderImpl extends AbstractPipedStreamSynchronizationBuilderImpl<PipedStreamSynchronizer.PipedOutputStreamSynchronizerBuilder>
      implements PipedStreamSynchronizer.PipedOutputStreamSynchronizerBuilder {
    private PipedOutputStream pipedOutputStream;

    @Override
    public PipedOutputStreamSynchronizerBuilder pipedOutputStream(PipedOutputStream pipedOutputStream) {
      this.pipedOutputStream = pipedOutputStream;
      return this;
    }

    @Override
    public PipedStreamSynchronizer build() {
      return new PipedOutputStreamSynchronizer(
          getAuthentication(),
          getOwncloudFileResource(),
          getOwncloudRestProperties(),
          getRestOperations(),
          getUriResolver(),
          pipedOutputStream);
    }

  }

}
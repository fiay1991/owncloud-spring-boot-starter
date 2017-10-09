/*-
 * #%L
 * owncloud-spring-boot-starter
 * %%
 * Copyright (C) 2016 - 2017 by the original Authors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package software.coolstuff.springframework.owncloud.model;

import org.springframework.http.MediaType;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * Abstract Representation of a Owncloud Resource.
 * <p/>
 * This Representation can be either a regular File or a Directory.
 *
 * @see OwncloudFileResource
 * @author mufasa1976
 * @since 1.2.0
 */
public interface OwncloudResource {

  /** URI of the Owncloud Resource based by the User Directory */
  URI getHref();

  /** Name of the Resource */
  String getName();

  /** Last modification Date */
  LocalDateTime getLastModifiedAt();

  /** Media Type of the Resource */
  MediaType getMediaType();

  /** ETag of the Resource (used by Synchronization) */
  String getETag();

}

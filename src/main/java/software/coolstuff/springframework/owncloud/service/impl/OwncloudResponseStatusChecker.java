package software.coolstuff.springframework.owncloud.service.impl;

import software.coolstuff.springframework.owncloud.exception.OwncloudStatusException;

@FunctionalInterface
interface OwncloudResponseStatusChecker {

  void checkForFailure(String uri, Ocs.Meta meta) throws OwncloudStatusException;

}

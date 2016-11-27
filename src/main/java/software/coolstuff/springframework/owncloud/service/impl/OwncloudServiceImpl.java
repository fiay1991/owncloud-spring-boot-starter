package software.coolstuff.springframework.owncloud.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import software.coolstuff.springframework.owncloud.service.api.OwncloudUserModificationService;
import software.coolstuff.springframework.owncloud.service.api.OwncloudUserQueryService;

class OwncloudServiceImpl extends AbstractOwncloudServiceImpl implements OwncloudUserQueryService, OwncloudUserModificationService {

  public OwncloudServiceImpl(RestTemplateBuilder builder) {
    super(builder);
  }

  @Override
  public List<String> getAllUsers(String filter) {
    OcsUsers users = null;
    if (StringUtils.isBlank(filter)) {
      users = getForObject("/users", OcsUsers.class);
    } else {
      users = getForObject("/users?search={filter}", OcsUsers.class, filter);
    }
    return users.getData().getUsers();
  }

  @Override
  public List<String> getAllGroups(String filter) {
    OcsGroups groups = null;
    if (StringUtils.isBlank(filter)) {
      groups = getForObject("/groups", OcsGroups.class);
    } else {
      groups = getForObject("/groups?search={filter}", OcsGroups.class, filter);
    }
    return groups.getData().getGroups();
  }

  @Override
  public List<String> getAllMembersOfGroup(String group) {
    Validate.notBlank(group);
    OcsUsers users = getForObject("/groups/{group}", OcsUsers.class, group);
    return users.getData().getUsers();
  }

}

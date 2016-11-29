package software.coolstuff.springframework.owncloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "owncloud")
public class OwncloudProperties {
  private String location;
  private boolean useAuthentication = false;
  private String username;
  private String password;
  private boolean enableModification = false;
}
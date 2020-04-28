package saml.example.idp;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import saml.example.core.SecurityConfiguration;

@Getter
@Setter
@Component
public class IdpConfiguration extends SecurityConfiguration {

  private String defaultEntityId;
  private Map<String, List<String>> attributes = new TreeMap<>();
  private final String idpPrivateKey;
  private final String idpCertificate;

  @Autowired
  public IdpConfiguration(JKSKeyManager keyManager,
                          @Value("${idp.entity_id}") String defaultEntityId,
                          @Value("${idp.private_key}") String idpPrivateKey,
                          @Value("${idp.certificate}") String idpCertificate) {
	super(keyManager);

    this.defaultEntityId = defaultEntityId;
    this.idpPrivateKey = idpPrivateKey;
    this.idpCertificate = idpCertificate;
    
    setEntityId(defaultEntityId);
    resetKeyStore(defaultEntityId, idpPrivateKey, idpCertificate);
    setSignatureAlgorithm(getDefaultSignatureAlgorithm());
  }

}

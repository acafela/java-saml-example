package saml.example.idp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class IdpConfiguration {

	@JsonIgnore
	private JKSKeyManager keyManager;
	private String entityId;
	private final String idpPrivateKey;
	private final String idpCertificate;

	@Autowired
	public IdpConfiguration(JKSKeyManager keyManager,
							@Value("${idp.entity_id}") String entityId,
							@Value("${idp.private_key}") String idpPrivateKey,
							@Value("${idp.certificate}") String idpCertificate) {
		this.keyManager = keyManager;
		this.entityId = entityId;
		this.idpPrivateKey = idpPrivateKey;
		this.idpCertificate = idpCertificate;

	}

}

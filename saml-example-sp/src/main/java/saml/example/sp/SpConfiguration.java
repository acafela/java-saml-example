package saml.example.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class SpConfiguration {
	
	@JsonIgnore
	private JKSKeyManager keyManager;

	private String entityId;
	private String idpSsoServiceUrl;
	private String protocolBinding;
	private boolean needsSigning;
	private String assertionConsumerServiceUrl;
	private String spPrivateKey;
	private String spCertificate;

	@Autowired
	public SpConfiguration(JKSKeyManager keyManager,
			@Value("${sp.base_url}") String spBaseUrl,
			@Value("${sp.entity_id}") String entityId,
			@Value("${sp.single_sign_on_service_location}") String idpSsoServiceUrl,
			@Value("${sp.acs_location_path}") String assertionConsumerServiceURLPath,
			@Value("${sp.protocol_binding}") String defaultProtocolBinding,
			@Value("${sp.private_key}") String spPrivateKey,
			@Value("${sp.certificate}") String spCertificate,
			@Value("${sp.needs_signing}") boolean needsSigning) {
		this.keyManager = keyManager;
		this.entityId = entityId;
		this.idpSsoServiceUrl = idpSsoServiceUrl;
		this.assertionConsumerServiceUrl = spBaseUrl + assertionConsumerServiceURLPath;
		this.protocolBinding = defaultProtocolBinding;
		this.spPrivateKey = spPrivateKey;
		this.spCertificate = spCertificate;
		this.needsSigning = needsSigning;
	}

}

package saml.example.core;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = "nameID")
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor
@ToString
public class SAMLPrincipal implements Principal {

	private String serviceProviderEntityID;
	private String requestID;
	private String assertionConsumerServiceURL;
	private String relayState;

	@NonNull private List<SAMLAttribute> attributes;
	@NonNull private String nameID;
	@NonNull private String nameIDType;

	public SAMLPrincipal(String nameID, String nameIDType, List<SAMLAttribute> attributes) {
		this.nameID = nameID;
		this.nameIDType = nameIDType;
		this.attributes = new ArrayList<>();
		this.attributes.addAll(attributes);
	}
	
	public static SAMLPrincipalBuilder builder(String nameID, String nameIDType, List<SAMLAttribute> attributes) {
        return hiddenBuilder().nameID(nameID).nameIDType(nameIDType).attributes(attributes);
    }

	@Override
	public String getName() {
		return nameID;
	}
}

package saml.example.sp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.GrantedAuthority;
import saml.example.core.SAMLBuilder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SpUser implements Principal {

	public SpUser() {}

	public SpUser(String username, List<Attribute> samlAttributes) {
		this.username = username;
		for(Attribute attr : samlAttributes) {
			String attrName  = attr.getName();
			if(attrName.equals("User.Username")) {
				this.username =  SAMLBuilder.getStringFromXMLObject(attr.getAttributeValues().get(0));
				
			} else if(attrName.equals("User.Email")) {
				this.email = SAMLBuilder.getStringFromXMLObject(attr.getAttributeValues().get(0));
				
			} else if(attrName.equals("User.FederationIdentifier")) {
				this.federationIdentifier = SAMLBuilder.getStringFromXMLObject(attr.getAttributeValues().get(0));
				
			} else if(attrName.equals("User.Department")) {
				this.department = SAMLBuilder.getStringFromXMLObject(attr.getAttributeValues().get(0));
				
			} else if(attrName.equals("User.DisplayName")) {
				this.displayName = SAMLBuilder.getStringFromXMLObject(attr.getAttributeValues().get(0));
				
			}
		}
	}

	private String username;
	private String email;
	private String federationIdentifier;
	private String department;
	private String displayName;

	private List<GrantedAuthority> roles = new ArrayList<>();

	@Override
	public String getName() {
		return this.username;
	}
	
}

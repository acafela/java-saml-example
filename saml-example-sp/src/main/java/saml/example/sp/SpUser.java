package saml.example.sp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SpUser implements Principal {

    private String username;
    private String email;
    private String department;
    private String displayName;
    private String federationIdentifier;
    private List<GrantedAuthority> roles = new ArrayList<>();

    public SpUser(String username, List<Attribute> samlAttributes) {
        this.username = username;
        for (Attribute attr : samlAttributes) {
            String attrName  = attr.getName();
            if (attrName.equals("User.Department")) {
                department = SAMLUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
            } else if (attrName.equals("User.Email")) {
                email = SAMLUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
            } else if (attrName.equals("User.DisplayName")) {
                displayName = SAMLUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
            } else if (attrName.equals("User.FederationIdentifier")) {
                federationIdentifier = SAMLUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
            }
        }
    }

    @Override
    public String getName() {
        return username;
    }

}
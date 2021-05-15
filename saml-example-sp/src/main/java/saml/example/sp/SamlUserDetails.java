package saml.example.sp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
final class SamlUserDetails implements UserDetails {

    private String username;
    private String email;
    private String department;
    private String displayName;
    private String federationIdentifier;
    private List<GrantedAuthority> authorities = new ArrayList<>();

    public SamlUserDetails(String username, List<Attribute> samlAttributes) {
        this.username = username;
        for (Attribute attr : samlAttributes) {
            String attrName  = attr.getName();
            switch (attrName) {
                case "User.Department":
                    department = SamlUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
                    break;
                case "User.Email":
                    email = SamlUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
                    break;
                case "User.DisplayName":
                    displayName = SamlUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
                    break;
                case "User.FederationIdentifier":
                    federationIdentifier = SamlUtil.getStringFromXMLObject(attr.getAttributeValues().get(0));
                    break;
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
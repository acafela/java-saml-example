package saml.example.idp;

import java.nio.file.attribute.UserPrincipal;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.security.auth.Subject;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@SuppressWarnings("serial")
public class LocalUserPrincipal implements UserPrincipal {

    @NonNull private Collection<? extends GrantedAuthority> authorities;
    private String userPrincipalName;
    private String department;
    private String displayName;
    private String password;
    private String mail;

    @Override
    public String getName() {
        return userPrincipalName;
    }
}
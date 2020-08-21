package saml.example.sp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 인증 제공
 */
@Component
public class SpAuthenticationProvider {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public Authentication provideAuthentication(Principal principal){

        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_USER");
        Authentication authentication = new RememberMeAuthenticationToken(principal.getName(), principal, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;

    }

}

package saml.example.idp;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.SessionCookieConfig;

import org.opensaml.common.binding.security.IssueInstantRule;
import org.opensaml.saml2.binding.decoding.HTTPRedirectDeflateDecoder;
import org.opensaml.saml2.binding.encoding.HTTPPostSimpleSignEncoder;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.ws.security.provider.BasicSecurityPolicy;
import org.opensaml.ws.security.provider.StaticSecurityPolicyResolver;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Value("${idp.sso_url}")
    private String ssoUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/error").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login").permitAll()
                .failureUrl("/login?error=true").permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/login")
                .and()
            .addFilterAfter(samlResponseFilter(), FilterSecurityInterceptor.class)
            .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/favicon.ico", "/*.js");
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                                .username("user")
                                .password("user123")
                                .roles("USER")
                                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                                .username("admin")
                                .password("admin123")
                                .roles("ADMIN")
                                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SamlResponseFilter samlResponseFilter() {
        return new SamlResponseFilter(ssoUrl);
    }

    @Bean
    public JKSKeyManager keyManager(@Value("${idp.entity_id}") String idpEntityId,
                                    @Value("${idp.private_key}") String idpPrivateKey,
                                    @Value("${idp.certificate}") String idpCertificate,
                                    @Value("${idp.passphrase}") String idpPassphrase) throws Exception {
        KeyStore keyStore = KeyStoreLocator.createKeyStore(idpPassphrase);
        KeyStoreLocator.addPrivateKey(keyStore, idpEntityId, idpPrivateKey, idpCertificate, idpPassphrase);
        return new JKSKeyManager(keyStore, Collections.singletonMap(idpEntityId, idpPassphrase), idpEntityId);
    }

    @Bean
    public LocalSamlPrincipalFactory samlPrincipalFactory() {
        return new LocalSamlPrincipalFactory(NameIDType.UNSPECIFIED);
    }

    @Bean
    public SamlMessageHandler samlMessageHandler(@Value("${idp.entity_id}") String idpEntityId,
                                                 @Value("${idp.clock_skew}") int clockSkew,
                                                 @Value("${idp.expires}") int expires,
                                                 JKSKeyManager keyManager) throws XMLParserException {
        StaticBasicParserPool parserPool = new StaticBasicParserPool();
        parserPool.initialize();
        BasicSecurityPolicy securityPolicy = new BasicSecurityPolicy();
        securityPolicy.getPolicyRules().addAll(Collections.singletonList(new IssueInstantRule(clockSkew, expires)));

        HTTPRedirectDeflateDecoder httpRedirectDeflateDecoder = new HTTPRedirectDeflateDecoder(parserPool);
        HTTPPostSimpleSignEncoder httpPostSimpleSignEncoder = new HTTPPostSimpleSignEncoder(
                VelocityFactory.getEngine(),
                "/templates/saml2-post-binding.vm",
                true
        );

        return new SamlMessageHandler(
                idpEntityId,
                keyManager,
                httpRedirectDeflateDecoder,
                httpPostSimpleSignEncoder,
                new StaticSecurityPolicyResolver(securityPolicy)
        );
    }
}
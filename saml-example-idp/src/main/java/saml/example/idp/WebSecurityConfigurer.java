package saml.example.idp;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.SessionCookieConfig;

import org.opensaml.common.binding.security.IssueInstantRule;
import org.opensaml.saml2.binding.decoding.HTTPRedirectDeflateDecoder;
import org.opensaml.saml2.binding.encoding.HTTPPostSimpleSignEncoder;
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer implements WebMvcConfigurer {

    @Bean
    public AuthenticationProvider authenticationProvider() {
        LocalUserPrincipal admin = LocalUserPrincipal.builder()
                                                .department("Development Team")
                                                .displayName("시스템관리자")
                                                .mail("administrator@xxx.com")
                                                .userPrincipalName("admin")
                                                .password("admin123")
                                                .authorities(Arrays.asList(
                                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                                        , new SimpleGrantedAuthority("ROLE_USER")))
                                                .build();
        LocalUserPrincipal user = LocalUserPrincipal.builder()
                                                .department("HR Team")
                                                .displayName("일반사용자")
                                                .mail("user123@xxx.com")
                                                .userPrincipalName("user")
                                                .password("user123")
                                                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                                                .build();
        return new LocalAuthenticationProvider(Arrays.asList(admin, user));
    }

    @Bean
    @Autowired
    public SAMLMessageHandler samlMessageHandler(@Value("${idp.entity_id}") String idpEntityId,
                                                 @Value("${idp.clock_skew}") int clockSkew,
                                                 @Value("${idp.expires}") int expires,
                                                 JKSKeyManager keyManager) throws XMLParserException {
        StaticBasicParserPool parserPool = new StaticBasicParserPool();
        parserPool.initialize();
        BasicSecurityPolicy securityPolicy = new BasicSecurityPolicy();
        securityPolicy.getPolicyRules().addAll(Arrays.asList(new IssueInstantRule(clockSkew, expires)));

        HTTPRedirectDeflateDecoder httpRedirectDeflateDecoder = new HTTPRedirectDeflateDecoder(parserPool);
        HTTPPostSimpleSignEncoder httpPostSimpleSignEncoder = new HTTPPostSimpleSignEncoder(
                VelocityFactory.getEngine(),
                "/templates/saml2-post-binding.vm",
                true
        );

        return new SAMLMessageHandler(
                idpEntityId,
                keyManager,
                httpRedirectDeflateDecoder,
                httpPostSimpleSignEncoder,
                new StaticSecurityPolicyResolver(securityPolicy)
        );
    }

    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Autowired
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
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setName("IdpSession");
            sessionCookieConfig.setHttpOnly(true);
        };
    }

    @Configuration
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/favicon.ico", "/*.css", "/*.js").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().hasRole("USER")
                    .and()
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .failureUrl("/login?error=true").permitAll()
                    .and()
                    .logout().logoutSuccessUrl("/");
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider);
        }

        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }
}
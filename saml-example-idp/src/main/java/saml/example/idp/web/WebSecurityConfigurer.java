package saml.example.idp.web;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.SessionCookieConfig;
import javax.xml.stream.XMLStreamException;

import org.opensaml.common.binding.decoding.URIComparator;
import org.opensaml.common.binding.security.IssueInstantRule;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import saml.example.core.KeyStoreLocator;
import saml.example.idp.IdpConfiguration;
import saml.example.idp.LocalAuthenticationProvider;
import saml.example.idp.LocalUserDetails;
import saml.example.idp.SAMLMessageHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer implements WebMvcConfigurer {
	
	@Bean
	public LocalAuthenticationProvider authenticationProvider() {
		LocalUserDetails admin = LocalUserDetails.builder().department("Development Team")
														.displayName("시스템관리자")
														.mail("administrator@xxx.com")
														.userPrincipalName("admin")
														.password("admin123")
														.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))).build();
		LocalUserDetails user = LocalUserDetails.builder().department("HR Team")
														.displayName("일반사용자")
														.mail("user123@xxx.com")
														.userPrincipalName("user")
														.password("user123")
														.authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))).build();
		return new LocalAuthenticationProvider(Arrays.asList(admin, user));
	}

	@Bean
	@Autowired
	public SAMLMessageHandler samlMessageHandler(@Value("${idp.clock_skew}") int clockSkew,
			@Value("${idp.expires}") int expires, @Value("${idp.base_url}") String idpBaseUrl,
			@Value("${idp.compare_endpoints}") boolean compareEndpoints, IdpConfiguration idpConfiguration,
			JKSKeyManager keyManager) throws XMLParserException, URISyntaxException {
		StaticBasicParserPool parserPool = new StaticBasicParserPool();
		BasicSecurityPolicy securityPolicy = new BasicSecurityPolicy();
		securityPolicy.getPolicyRules().addAll(Arrays.asList(new IssueInstantRule(clockSkew, expires)));

		HTTPRedirectDeflateDecoder httpRedirectDeflateDecoder = new HTTPRedirectDeflateDecoder(parserPool);
		HTTPPostDecoder httpPostDecoder = new HTTPPostDecoder(parserPool);
		if (!compareEndpoints) {
			URIComparator noopComparator = (uri1, uri2) -> true;
			httpPostDecoder.setURIComparator(noopComparator);
			httpRedirectDeflateDecoder.setURIComparator(noopComparator);
		}

		parserPool.initialize();
		HTTPPostSimpleSignEncoder httpPostSimpleSignEncoder = new HTTPPostSimpleSignEncoder(VelocityFactory.getEngine(), "/templates/saml2-post-simplesign-binding.vm", true);

		return new SAMLMessageHandler(keyManager, Arrays.asList(httpRedirectDeflateDecoder, httpPostDecoder),
				httpPostSimpleSignEncoder, new StaticSecurityPolicyResolver(securityPolicy), idpConfiguration,
				idpBaseUrl);
	}

	@Bean
	public static SAMLBootstrap samlBootstrap() {
		return new SAMLBootstrap();
	}

	@Autowired
	@Bean
	public JKSKeyManager keyManager(@Value("${idp.entity_id}") String idpEntityId,
			@Value("${idp.private_key}") String idpPrivateKey, @Value("${idp.certificate}") String idpCertificate,
			@Value("${idp.passphrase}") String idpPassphrase) throws InvalidKeySpecException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, IOException, XMLStreamException {
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
		private LocalAuthenticationProvider authenticationProvider;

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

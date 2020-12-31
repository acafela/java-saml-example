package saml.example.sp;

import javax.servlet.SessionCookieConfig;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return servletContext -> {
			SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
			sessionCookieConfig.setName("transaction.id");
			sessionCookieConfig.setHttpOnly(true);
		};
	}

	@Bean
	public SamlSsoEntryPoint samlSsoEntryPoint() {
		return new SamlSsoEntryPoint();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/", "/error", "/acs/**", "/sso/saml2").permitAll()
			.anyRequest().hasRole("USER")
			.and()
			.httpBasic().authenticationEntryPoint(samlSsoEntryPoint())
			.and()
			.cors()
			.and()
			.csrf().disable()
			.logout().logoutSuccessUrl("/");
	}

	@Bean
	public SpAuthenticationSuccessHandler spAuthenticationSuccessHandler(){
		SpAuthenticationSuccessHandler successHandler = new SpAuthenticationSuccessHandler();
		successHandler.setRedirectUrl("/user");
		return successHandler;
	}

	@Bean
	public VelocityEngine velocityEngine() {
		return VelocityFactory.getEngine();
	}

	@Bean(initMethod = "initialize")
	public ParserPool parserPool() {
		return new StaticBasicParserPool();
	}
}
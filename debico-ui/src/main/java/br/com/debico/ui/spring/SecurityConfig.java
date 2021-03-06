package br.com.debico.ui.spring;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.jasypt.springsecurity3.authentication.encoding.PasswordEncoder;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.debico.core.helpers.Roles;
import br.com.debico.core.spring.config.ServicesConfig;
import br.com.debico.social.spring.DebicoSocialSpringSocialConfigurer;

/**
 * Classe de configuração do Spring Security.
 * <p/>
 * Deve ser utilizada em conjunto com a configuração da camada de serviços.
 * 
 * @author r_fernandes
 * @see <a
 *      href="http://docs.spring.io/spring-security/site/docs/3.2.x/guides/helloworld.html">Hello
 *      Spring Security Java Config</a>
 * @see <a
 *      href="http://docs.spring.io/spring-security/site/docs/3.2.x/guides/hellomvc.html">Hello
 *      Spring MVC Security Java Config</a>
 * @see ServicesConfig
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Inject
	private UserDetailsService userDetailsService;

	@Inject
	private PasswordEncryptor passwordEncryptor;
	
	@Inject
	private DataSource dataSource;

	/**
	 * @see <a href="http://www.jasypt.org/springsecurity.html">Integrating
	 *      Jasypt with Spring Security 2.x or 3.x</a>
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = new PasswordEncoder();
		passwordEncoder.setPasswordEncryptor(passwordEncryptor);

		return passwordEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(this.userDetailsService)
			.passwordEncoder(this.passwordEncoder());
	}	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	        .ignoring()
	            .antMatchers("/robots.txt")
	            .antMatchers("/static/**");
	}
	
	/**
	 * Veja os exemplos de configuração nos links abaixo.
	 * 
	 * @see <a href="http://www.baeldung.com/spring-security-session">Spring Security Session Management</a>
	 * @see <a href="http://docs.spring.io/spring-security/site/docs/3.2.x/guides/form.html">Creating  a Custom Login Form</a>
	 * @see <a href="http://docs.spring.io/spring-security/site/docs/3.2.4.RELEASE/reference/htmlsingle/">Spring Secutiry Doc</a>
	 * @see <a href="http://www.mkyong.com/spring-security/spring-security-remember-me-example/">Spring Security Remember Me Example</a>
	 * @see <a href="http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#headers">Security HTTP Response Headers</a>
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement()
			.maximumSessions(5)
			.expiredUrl("/support/session_exp")
			.and()
		.and()
		    .authorizeRequests()
				.antMatchers("/contato/").permitAll()
				.antMatchers("/sucesso/").permitAll()
				.antMatchers("/support/**").permitAll()
				.antMatchers("/public/**").anonymous()
				.antMatchers("/connect/**").permitAll()
				.antMatchers(HttpMethod.POST, "/senha/").permitAll()
				.antMatchers("/campeonatos/**/palpite/**").access("!hasRole('" + Roles.ROLE_ADMIN + "') and isAuthenticated()")
				.antMatchers("/campeonatos/**/palpites/**").access("!hasRole('" + Roles.ROLE_ADMIN + "') and isAuthenticated()")
				.antMatchers("/api*/**").access("hasRole('" + Roles.ROLE_ADMIN + "')")
				.antMatchers("/admin/**").access("hasRole('" + Roles.ROLE_ADMIN + "')")
				.anyRequest().authenticated()
				.and()
				.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout=true")
					.permitAll()
				.and()
				.formLogin()
					.loginPage("/login")
					.permitAll()
					.failureUrl("/login?error=true")
					.defaultSuccessUrl("/")
				.and()
				.csrf()
				.and()
				.rememberMe()
				    .tokenValiditySeconds(1209600)
				    .tokenRepository(this.persistentTokenRepository())
				.and()
				    .apply(new DebicoSocialSpringSocialConfigurer()
				    		.signupUrl("/public/cadastro/social")
				    		.postFailureUrl("/support/error?msgCode=login.social.error"));
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
	    JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
	    jdbcTokenRepositoryImpl.setDataSource(dataSource);
	    
	    return jdbcTokenRepositoryImpl;
	}

}

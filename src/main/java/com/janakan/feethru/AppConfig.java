package com.janakan.feethru;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.auth0.AuthenticationController;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AppConfig extends WebSecurityConfigurerAdapter {
	@Value(value = "${com.auth0.domain}")
	private String domain;
	@Value(value = "${com.auth0.clientId}")
	private String clientId;
	@Value(value = "${com.auth0.clientSecret}")
	private String clientSecret;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/callback", "/login").permitAll().antMatchers("/**")
				.authenticated().and().logout().permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER).and().formLogin().loginPage("/login");
	}

	@Bean
	public AuthenticationController authenticationController() throws UnsupportedEncodingException {
		return AuthenticationController.newBuilder(domain, clientId, clientSecret).build();
	}

	public String getDomain() {
		return domain;
	}
}
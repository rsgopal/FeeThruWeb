package com.janakan.feethru;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenAuthentication extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -4373026320138221509L;

	private final DecodedJWT jwt;
	private boolean invalidated;

	public TokenAuthentication(DecodedJWT jwt) {
		super(readAuthorities(jwt));
		this.jwt = jwt;
	}

	private boolean hasExpired() {
		return jwt.getExpiresAt().before(new Date());
	}

	private static Collection<? extends GrantedAuthority> readAuthorities(DecodedJWT jwt) {
		Claim rolesClaim = jwt.getClaim("https://access.control/roles");
		return Stream.of(rolesClaim).filter(rc -> !rc.isNull()).map(rc -> rc.asArray(String.class)).flatMap(Stream::of)
				.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

	@Override
	public String getCredentials() {
		return jwt.getToken();
	}

	@Override
	public Object getPrincipal() {
		return jwt.getSubject();
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		if (authenticated) {
			throw new IllegalArgumentException("Create a new Authentication object to authenticate");
		}
		invalidated = true;
	}

	@Override
	public boolean isAuthenticated() {
		return !invalidated && !hasExpired();
	}
}
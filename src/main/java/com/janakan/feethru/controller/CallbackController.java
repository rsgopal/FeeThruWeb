package com.janakan.feethru.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.Tokens;
import com.auth0.jwt.JWT;
import com.janakan.feethru.TokenAuthentication;

@Controller
@RequestMapping("/callback")
public class CallbackController {
	protected static final String LOGIN_PATH = "/login";

	@Autowired
	private AuthenticationController controller;

	@GetMapping
	protected void getCallback(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		handle(req, res);
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	protected void postCallback(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		handle(req, res);
	}

	private void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			Tokens tokens = controller.handle(req);
			TokenAuthentication tokenAuth = new TokenAuthentication(JWT.decode(tokens.getIdToken()));
			SecurityContextHolder.getContext().setAuthentication(tokenAuth);
			res.sendRedirect("/");
		} catch (AuthenticationException | IdentityVerificationException e) {
			System.out.println("Authentication failed. " + e.getClass() + "; " + e.getMessage());
			SecurityContextHolder.clearContext();
			res.sendRedirect(LOGIN_PATH);
		}
	}
}
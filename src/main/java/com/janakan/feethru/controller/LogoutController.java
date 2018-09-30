package com.janakan.feethru.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LogoutController {
	protected static final String LOGOUT_PATH = "/logout";

	@RequestMapping(value = LOGOUT_PATH, method = RequestMethod.GET)
	protected String logout(final HttpServletRequest req) {
		Optional.ofNullable(req.getSession()).ifPresent(HttpSession::invalidate);
		return "redirect:" + req.getContextPath() + CallbackController.LOGIN_PATH;
	}
}
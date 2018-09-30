package com.janakan.feethru.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth0.AuthenticationController;
import com.janakan.feethru.AppConfig;

@Controller
public class LoginController {
	@Autowired
	private AuthenticationController controller;
	@Autowired
	private AppConfig appConfig;

	@RequestMapping(value = CallbackController.LOGIN_PATH, method = RequestMethod.GET)
	protected String login(final HttpServletRequest req) {
		StringBuffer requestURL = req.getRequestURL();
		StringBuffer redirectUrl = requestURL
				.delete(requestURL.length() - req.getRequestURI().length(), requestURL.length()).append("/callback");
		String authorizeUrl = controller.buildAuthorizeUrl(req, redirectUrl.toString())
				.withAudience(String.format("https://%s/userinfo", appConfig.getDomain())).build();
		return "redirect:" + authorizeUrl;
	}
}

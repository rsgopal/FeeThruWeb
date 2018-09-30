package com.janakan.feethru.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	private static final String PATH = "/error";

	@RequestMapping(PATH)
	protected String error(final RedirectAttributes redirectAttributes) throws IOException {
		redirectAttributes.addFlashAttribute("error", true);
		return "redirect:" + CallbackController.LOGIN_PATH;
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
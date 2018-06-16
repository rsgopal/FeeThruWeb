package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ToString
@Getter
@AllArgsConstructor
public class Message {
	public static final String PROPERTY = "messages";

	public enum Severity {
		info, success, warning, danger;
	}

	private Severity severity;
	private String message;

	@SuppressWarnings("unchecked")
	public static void addMessage(Model model, Severity severity, String message) {
		List<Message> msgs = (List<Message>) model.asMap().get(PROPERTY);
		if (msgs == null) {
			msgs = new ArrayList<>();
			if (model instanceof RedirectAttributes) {
				((RedirectAttributes) model).addFlashAttribute(PROPERTY, msgs);
			} else {
				model.addAttribute(PROPERTY, msgs);
			}
		}
		msgs.add(new Message(severity, message));
	}
}

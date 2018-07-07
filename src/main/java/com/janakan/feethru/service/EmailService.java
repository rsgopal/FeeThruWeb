package com.janakan.feethru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String msg) {
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(to);
		simpleMessage.setSubject(subject);
		simpleMessage.setText(msg);
		mailSender.send(simpleMessage);
	}
}

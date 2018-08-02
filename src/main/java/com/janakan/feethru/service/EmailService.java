package com.janakan.feethru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.janakan.feethru.model.Account;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(Account account, String subject, String msg) {
		if (account.isEmailVerified()) {
			sendEmail(account, subject, msg);
		}
	}

	public void sendEmail(String email, String subject, String msg) {
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(email);
		simpleMessage.setSubject(subject);
		simpleMessage.setText(msg);
		mailSender.send(simpleMessage);
	}
}

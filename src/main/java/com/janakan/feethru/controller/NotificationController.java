package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janakan.feethru.controller.Message.Severity;
import com.janakan.feethru.model.Account;
import com.janakan.feethru.model.Notification;
import com.janakan.feethru.repository.AccountsRepository;
import com.janakan.feethru.repository.InvoicesRepository;

import lombok.Data;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
	@Data
	public static class NotificationList {
		private List<Notification> items = new ArrayList<>();
	}

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private InvoicesRepository invoicesRepository;

	@Autowired
	private AccountsRepository accountsRepository;

	@GetMapping("")
	public String main(Model model) {
		NotificationList notificationList = new NotificationList();
		List<Notification> notifications = invoicesRepository.findAllByIsClosed(false).stream()
				.map(invoice -> invoice.toNotification()).collect(Collectors.toList());
		notificationList.setItems(notifications);
		model.addAttribute("notificationList", notificationList);
		return "notifications/index";
	}

	@PostMapping("/send")
	public String send(NotificationList notificationList, RedirectAttributes redirectAttributes) {
		notificationList.getItems().stream().filter(notif -> notif.getInvoice() != null).forEach(notif -> {
			SimpleMailMessage simpleMessage = new SimpleMailMessage();
			Account account = notif.getInvoice().getAccount();
			simpleMessage.setTo(account.getEmail());
			simpleMessage.setSubject(notif.getSubject());
			simpleMessage.setText(notif.getMessage());
			mailSender.send(simpleMessage);
			account.setLastNotificationDate(new Date());
			accountsRepository.save(account);
		});
		Message.addMessage(redirectAttributes, Severity.info, "Notification sent.");
		return "redirect:/";
	}
}

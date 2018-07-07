package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.janakan.feethru.service.EmailService;

import lombok.Data;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
	@Data
	public static class NotificationList {
		private Collection<Notification> items = new ArrayList<>();
	}

	@Autowired
	private EmailService emailService;

	@Autowired
	private InvoicesRepository invoicesRepository;

	@Autowired
	private AccountsRepository accountsRepository;

	@GetMapping("")
	public String main(Model model) {
		NotificationList notificationList = new NotificationList();
		Set<Notification> notifications = new TreeSet<Notification>(invoicesRepository.findAllByIsClosed(false).stream()
				.map(invoice -> invoice.toNotification()).collect(Collectors.toList()));
		notificationList.setItems(notifications);
		model.addAttribute("notificationList", notificationList);
		return "notifications/index";
	}

	@PostMapping("/send")
	public String send(NotificationList notificationList, RedirectAttributes redirectAttributes) {
		notificationList.getItems().stream().filter(notif -> notif.getInvoice() != null).forEach(notif -> {
			Account account = notif.getInvoice().getAccount();
			emailService.sendEmail(account.getEmail(), notif.getSubject(), notif.getMessage());
			account.setLastNotificationDate(new Date());
			accountsRepository.save(account);
		});
		Message.addMessage(redirectAttributes, Severity.info, "Notification sent.");
		return "redirect:/";
	}
}

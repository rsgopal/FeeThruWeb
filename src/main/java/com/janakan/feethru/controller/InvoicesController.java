package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janakan.feethru.model.Account;
import com.janakan.feethru.model.Invoice;
import com.janakan.feethru.repository.AccountsRepository;
import com.janakan.feethru.repository.InvoicesRepository;

import lombok.Data;

@Controller
@RequestMapping("/invoices")
public class InvoicesController {
	@Data
	public static class InvoiceList {
		private List<Invoice> items = new ArrayList<>();
	}

	@Autowired
	private InvoicesRepository invoicesRepository;

	@Autowired
	private AccountsRepository accountsRepository;

	@GetMapping("")
	public String index(@RequestParam(required = false) boolean showAll, Model model) {
		List<Invoice> invoices = showAll ? invoicesRepository.findAll() : invoicesRepository.findAllByIsClosed(false);
		model.addAttribute("invoices", invoices);
		return "invoices/index";
	}

	@GetMapping("/generate")
	public String create(Model model) {
		InvoiceList invoiceList = new InvoiceList();
		List<Account> activeAccounts = accountsRepository.findAllByIsActive(true);
		activeAccounts.stream().forEach(account -> invoiceList.getItems().add(account.toInvoice()));
		model.addAttribute("invoiceList", invoiceList);
		return "invoices/generate";
	}

	@PostMapping("/save")
	public String save(InvoiceList invoiceList, RedirectAttributes redirectAttributes) {
		List<Invoice> invoicesToSave = invoiceList.getItems().stream().filter(invoice -> invoice.getAccount() != null)
				.filter(invoice -> invoice.getAmount() > 0).map(invoice -> {
					List<Invoice> invoices = invoicesRepository.findAllByAccount(invoice.getAccount().getId()).stream()
							.map(currInvoice -> {
								currInvoice.setClosed(true);
								currInvoice.setCloseDate(new Date());
								return currInvoice;
							}).collect(Collectors.toList());
					invoice.setBillDate(new Date());
					invoices.add(invoice);
					return invoices;
				}).flatMap(List::stream).collect(Collectors.toList());
		invoicesRepository.saveAll(invoicesToSave);
		List<Account> accounts = invoicesToSave.stream().filter(invoice -> !invoice.isClosed())
				.map(invoice -> invoice.getUpdatedAccount()).collect(Collectors.toList());
		accountsRepository.saveAll(accounts);
		return "redirect:/invoices";
	}
}

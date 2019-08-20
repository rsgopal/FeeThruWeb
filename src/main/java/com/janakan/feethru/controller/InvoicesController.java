package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		List<Invoice> invoices = showAll ? invoicesRepository.findAll(Sort.by("closeDate").descending())
				: invoicesRepository.findAllByIsClosed(false);
		model.addAttribute("invoices", invoices);
		return "invoices/index";
	}

	@GetMapping("/generate")
	public String create(Model model) {
		InvoiceList invoiceList = new InvoiceList();
		List<Account> activeAccounts = accountsRepository.findAllByIsActive(true);
		invoiceList.setItems(activeAccounts.stream().map(account -> account.toInvoice()).collect(Collectors.toList()));
		model.addAttribute("invoiceList", invoiceList);
		return "invoices/generate";
	}

	@PostMapping("/save")
	public String save(InvoiceList invoiceList, RedirectAttributes redirectAttributes) {
		List<Invoice> invoicesToSave = invoiceList.getItems().stream().filter(invoice -> invoice.getAccount() != null)
				.filter(invoice -> invoice.getAmount() > 0).map(invoice -> {
					List<Invoice> invoices = invoicesRepository
							.findAllByAccountAndIsClosed(invoice.getAccount().getId(), false).stream()
							.map(this::closePastOpenInvoice).collect(Collectors.toList());
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

	private Invoice closePastOpenInvoice(Invoice pastOpenInvoice) {
		pastOpenInvoice.setClosed(true);
		pastOpenInvoice.setCloseDate(new Date());
		return pastOpenInvoice;
	}

	@PostMapping("/delete/{id}")
	@Transactional
	public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Optional<Invoice> optInvoice = invoicesRepository.findById(id);
		if (optInvoice.isPresent()) {
			Invoice invoice = optInvoice.get();
			Account account = invoice.getAccount();
			account.credit(invoice.getAmount());
			accountsRepository.save(account);
			invoicesRepository.delete(invoice);
		}
		return "redirect:/invoices";
	}
}

package com.janakan.feethru.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janakan.feethru.controller.Message.Severity;
import com.janakan.feethru.model.Account;
import com.janakan.feethru.model.Invoice;
import com.janakan.feethru.repository.AccountsRepository;
import com.janakan.feethru.repository.InvoicesRepository;

@Controller
@RequestMapping("/accounts")
public class AccountsController {
	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	private InvoicesRepository invoicesRepository;

	@GetMapping("")
	public String index(Model model) {
		List<Account> accounts = accountsRepository.findAll();
		model.addAttribute("accounts", accounts);
		return "accounts/index";
	}

	@GetMapping("/create")
	public String create(Model model) {
		Account account = new Account();
		model.addAttribute("account", account);
		return "accounts/upsert";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		Account account = loadAccount(id, redirectAttributes);
		if (account != null) {
			model.addAttribute("account", account);
			return "accounts/upsert";
		}
		return "redirect:accounts";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Account account = loadAccount(id, redirectAttributes);
		if (account != null) {
			List<Invoice> invoices = invoicesRepository.findAllByAccount(id);
			invoicesRepository.deleteAll(invoices);
			accountsRepository.deleteById(id);
			Message.addMessage(redirectAttributes, Severity.success,
					account.getName() + "'s account successfully deleted.");
		}
		return "redirect:/accounts";
	}

	@GetMapping("/disable/{id}")
	public String disable(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Account account = loadAccount(id, redirectAttributes);
		if (account != null) {
			account.setActive(false);
			accountsRepository.save(account);
			Message.addMessage(redirectAttributes, Severity.success,
					account.getName() + "'s account successfully disabled.");
		}
		return "redirect:/accounts";
	}

	@GetMapping("/activate/{id}")
	public String activate(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Account account = loadAccount(id, redirectAttributes);
		if (account != null) {
			account.setActive(true);
			accountsRepository.save(account);
			Message.addMessage(redirectAttributes, Severity.success,
					account.getName() + "'s account successfully disabled.");
		}
		return "redirect:/accounts";
	}

	@PostMapping("/save")
	public String save(Account account, RedirectAttributes redirectAttributes) {
		if (account.getId() == null) {
			accountsRepository.insert(account);
		} else {
			Account currAccount = loadAccount(account.getId(), redirectAttributes);
			account.update(currAccount);
			accountsRepository.save(account);
		}
		Message.addMessage(redirectAttributes, Severity.success, "Account successfully saved.");
		return "redirect:/accounts";
	}

	private Account loadAccount(String id, Model model) {
		Optional<Account> accountOpt = accountsRepository.findById(id);
		if (accountOpt.isPresent()) {
			return accountOpt.get();
		}
		Message.addMessage(model, Severity.danger, "Unable to find account with ID " + id);
		return null;
	}
}

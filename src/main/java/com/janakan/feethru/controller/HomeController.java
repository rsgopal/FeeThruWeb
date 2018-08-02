package com.janakan.feethru.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janakan.feethru.controller.Message.Severity;
import com.janakan.feethru.model.Account;
import com.janakan.feethru.model.Invoice;
import com.janakan.feethru.model.Transaction;
import com.janakan.feethru.model.Transaction.Type;
import com.janakan.feethru.repository.AccountsRepository;
import com.janakan.feethru.repository.InvoicesRepository;
import com.janakan.feethru.repository.TransactionRepository;
import com.janakan.feethru.service.EmailService;

@Controller
public class HomeController {
	public enum TransactionType {
		paid {
			@Override
			public float[] getPaidAndDiscountAmounts(float invoiceAmount, float amount) {
				return new float[] { amount, 0 };
			}
		},
		paidAndExcused {
			@Override
			public float[] getPaidAndDiscountAmounts(float invoiceAmount, float amount) {
				return new float[] { amount, invoiceAmount - amount };
			}
		},
		excused {
			@Override
			public float[] getPaidAndDiscountAmounts(float invoiceAmount, float amount) {
				return new float[] { 0, amount };
			}
		};

		public abstract float[] getPaidAndDiscountAmounts(float invoiceAmount, float amount);
	}

	@Autowired
	private InvoicesRepository invoicesRepository;

	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	private TransactionRepository transactionReposiory;

	@Autowired
	private EmailService emailService;

	@GetMapping("")
	public String main(Model model) {
		Set<Invoice> invoices = new TreeSet<Invoice>(invoicesRepository.findAllByIsClosed(false));
		if (invoices.isEmpty()) {
			Message.addMessage(model, Severity.warning, "There are no open invoices. Go to Invoices to generate them.");
		}
		model.addAttribute("invoices", invoices);
		return "home";
	}

	@PostMapping(value = "/transaction")
	public String transaction(String id, TransactionType transactionType, float amount,
			RedirectAttributes redirectAttributes) {
		Invoice invoice = loadInvoice(id, redirectAttributes);
		if (invoice != null) {
			Account account = invoice.getAccount();
			float[] paidAndDiscountAmounts = transactionType.getPaidAndDiscountAmounts(invoice.getAmount(), amount);
			invoicesRepository.save(invoice.close());
			transactionReposiory
					.saveAll(newTransactions(account, paidAndDiscountAmounts[0], paidAndDiscountAmounts[1]));
			accountsRepository.save(account.credit(paidAndDiscountAmounts[0] + paidAndDiscountAmounts[1]));
			//emailService.sendEmail(account, "Payment Received",
			//		"Thank you for your dance class payment of $" + (int) paidAndDiscountAmounts[0] + ".");
			Message.addMessage(redirectAttributes, Severity.success,
					"Transaction for " + account.getDisplayName() + " successfully processed.");
		}
		return "redirect:/";
	}

	private Invoice loadInvoice(String id, Model model) {
		Optional<Invoice> invoiceOpt = invoicesRepository.findById(id);
		return invoiceOpt.orElseGet(() -> {
			Message.addMessage(model, Severity.danger, "Unable to find invoice with ID " + id);
			return null;
		});
	}

	private Collection<Transaction> newTransactions(Account account, float payAmount, float excusedAmount) {
		Collection<Transaction> transactions = new ArrayList<>();
		if (payAmount > 0) {
			transactions.add(new Transaction(null, account, payAmount, Type.credit, "Regular Payment", new Date()));
		}
		if (excusedAmount > 0) {
			transactions.add(new Transaction(null, account, excusedAmount, Type.discount,
					"Excused $" + excusedAmount + (payAmount > 0 ? " after payment of $" + payAmount : ""),
					new Date()));
		}
		return transactions;
	}
}

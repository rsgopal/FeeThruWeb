package com.janakan.feethru.controller;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.janakan.feethru.controller.Message.Severity;
import com.janakan.feethru.model.Transaction;
import com.janakan.feethru.model.Transaction.Type;
import com.janakan.feethru.repository.TransactionsRepository;

import lombok.Data;

@Controller
@RequestMapping("/transactions")
public class TransactionsController {
	@Data
	public static class MinMaxDates {
		private Date minDate;
		private Date maxDate;
	}

	@Autowired
	private TransactionsRepository transactionsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private Transaction loadTransaction(String id, Model model) {
		Optional<Transaction> transactionOpt = transactionsRepository.findById(id);
		if (transactionOpt.isPresent()) {
			return transactionOpt.get();
		}
		Message.addMessage(model, Severity.danger, "Unable to find transaction with ID " + id);
		return null;
	}

	private Date newDate(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}

	@GetMapping("")
	public String index(@RequestParam(required = false) Optional<Integer> year, Model model) {
		Aggregation aggregation = Aggregation
				.newAggregation(Aggregation.group().min("date").as("minDate").max("date").as("maxDate"));
		AggregationResults<MinMaxDates> aggregateResult = mongoTemplate.aggregate(aggregation, Transaction.class,
				MinMaxDates.class);
		MinMaxDates minMaxDates = aggregateResult.getMappedResults().get(0);
		int minYear = minMaxDates.getMinDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		int maxYear = minMaxDates.getMaxDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		List<Integer> years = IntStream.range(minYear - 1, maxYear + 1).boxed().sorted(Collections.reverseOrder())
				.collect(Collectors.toList());
		int yearValueOrDef = year.orElse(Calendar.getInstance().get(Calendar.YEAR));
		int yearValue = years.stream().filter(yr -> yr == yearValueOrDef).findFirst().orElse(years.get(0));
		List<Transaction> transactions = transactionsRepository.findAllByDateBetween(Sort.by("date").descending(),
				newDate(yearValue), newDate(yearValue + 1));
		if (transactions.isEmpty()) {
			Message.addMessage(model, Severity.warning, "No transactions found.");
		}
		Map<Integer, List<Transaction>> transactionsByMonth = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getTransactionMonth,
						() -> new TreeMap<>(Collections.reverseOrder()), Collectors.toList()));
		Map<Integer, Map<Type, Double>> totalByMonthAndType = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getTransactionMonth,
						Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount))));
		Map<Type, Double> totalByType = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));
		model.addAttribute("transactionsByMonth", transactionsByMonth).addAttribute("years", years)
				.addAttribute("currentYear", yearValue).addAttribute("totalByMonthAndType", totalByMonthAndType)
				.addAttribute("totalByType", totalByType);
		return "transactions/index";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		Transaction transaction = loadTransaction(id, model);
		if (transaction != null) {
			model.addAttribute("transaction", transaction);
			return "transactions/update";
		}
		return "redirect:transactions";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Transaction transaction = loadTransaction(id, redirectAttributes);
		if (transaction != null) {
			transactionsRepository.deleteById(id);
			Message.addMessage(redirectAttributes, Severity.success, "Transaction successfully deleted.");
		}
		return "redirect:/transactions";
	}
}

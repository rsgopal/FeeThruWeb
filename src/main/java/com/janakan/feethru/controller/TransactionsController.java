package com.janakan.feethru.controller;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.janakan.feethru.model.Transaction;
import com.janakan.feethru.repository.TransactionRepository;

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
	private TransactionRepository transactionRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	private Date newDate(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
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
		List<Integer> years = IntStream.range(minYear, maxYear + 1).boxed().collect(Collectors.toList());
		Integer yearValue = year.orElse(Calendar.getInstance().get(Calendar.YEAR));
		List<Transaction> transactions = transactionRepository.findAllByDateBetween(Sort.by("date"), newDate(yearValue),
				newDate(yearValue + 1));
		Map<Integer, Object> transactionsByMonth = transactions.stream().collect(Collectors.groupingBy(
				Transaction::getTransactionMonth, Collectors.collectingAndThen(Collectors.toList(), list -> {
					list.sort(Collections.reverseOrder());
					return list;
				})));
		System.out.println(transactionsByMonth);
		model.addAttribute("transactions", transactions).addAttribute("years", years);
		return "transactions/index";
	}
}

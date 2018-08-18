package com.janakan.feethru.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.janakan.feethru.model.Transaction;

public interface TransactionsRepository extends MongoRepository<Transaction, String> {
	public List<Transaction> findAllByDateBetween(Sort sort, Date start, Date end);
}

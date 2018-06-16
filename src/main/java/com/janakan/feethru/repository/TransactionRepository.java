package com.janakan.feethru.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.janakan.feethru.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}

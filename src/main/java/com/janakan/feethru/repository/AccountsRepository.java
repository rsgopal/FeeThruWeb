package com.janakan.feethru.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.janakan.feethru.model.Account;

public interface AccountsRepository extends MongoRepository<Account, String> {
	public List<Account> findAllByIsActive(boolean isActive);

	public List<Account> findAllByIsActiveAndBalanceGreaterThan(boolean isActive, float balance);
}

package com.janakan.feethru.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.janakan.feethru.model.Invoice;

public interface InvoicesRepository extends MongoRepository<Invoice, String> {
	List<Invoice> findAllByIsClosed(boolean isClosed);
	List<Invoice> findAllByAccount(String id);
	List<Invoice> findAllByAccountAndIsClosed(String id, boolean isClosed);
}

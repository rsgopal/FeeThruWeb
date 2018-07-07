package com.janakan.feethru.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	public enum Type {
		credit, debit, discount;
	}

	@Id
	private String id;
	@DBRef
	private Account account;
	private float amount;
	private Type type;
	private String desc;
	private Date date;
}

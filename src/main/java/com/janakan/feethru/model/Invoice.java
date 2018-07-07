package com.janakan.feethru.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Invoice implements Comparable<Invoice> {
	@Id
	private String id;
	private Date billDate;
	@DBRef
	private Account account;
	private float amount;
	private boolean isClosed;
	private Date closeDate;

	public Account getUpdatedAccount() {
		account.setBalance(amount);
		return account;
	}

	public Invoice close() {
		isClosed = true;
		closeDate = new Date();
		return this;
	}

	public Notification toNotification() {
		return new Notification(this);
	}

	@Override
	public int compareTo(Invoice o) {
		if (o == null || o.account == null || o.account.getDisplayName() == null) {
			return -1;
		}
		if (account == null || account.getDisplayName() == null) {
			return 1;
		}
		return account.getDisplayName().compareTo(o.account.getDisplayName());
	}
}

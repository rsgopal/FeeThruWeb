package com.janakan.feethru.model;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account implements Comparable<Account> {
	@Id
	private String id;
	private String name;
	private String desc;
	private String email;
	private float amount;
	private float balance;
	private boolean isActive = true;
	private boolean isEmailVerified = false;
	private Date lastNotificationDate;

	public String getDisplayName() {
		return Optional.ofNullable(name).orElse("<NONE>") + Optional.ofNullable(desc).map(String::trim)
				.filter(str -> !str.isEmpty()).map(str -> " (" + str + ")").orElse("");
	}

	public float getAmountDue() {
		return amount + balance;
	}

	public Invoice toInvoice() {
		Invoice invoice = new Invoice();
		invoice.setAccount(this);
		invoice.setAmount(getAmountDue());
		return invoice;
	}

	public Account update(Account currAccount) {
		this.balance = currAccount.balance;
		// this.isActive = currAccount.isActive;
		return this;
	}

	public Account credit(float amount) {
		balance -= amount;
		return this;
	}

	@Override
	public int compareTo(Account o) {
		if (o == null || o.getDisplayName() == null) {
			return -1;
		}
		if (getDisplayName() == null) {
			return 1;
		}
		return getDisplayName().compareTo(o.getDisplayName());
	}
}

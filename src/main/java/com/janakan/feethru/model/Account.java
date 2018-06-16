package com.janakan.feethru.model;

import java.util.Date;
import java.util.Objects;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {
	@Id
	private String id;
	private String name;
	private String desc;
	private String email;
	private float amount;
	private float balance;
	private boolean isActive = true;
	private Date lastNotificationDate;

	public String getDisplayName() {
		return name + (Objects.isNull(desc) || desc.trim().isEmpty() ? "" : " (" + desc.trim() + ")");
	}

	public float getAmountDue() {
		return amount + balance;
	}

	public Invoice toInvoice() {
		Invoice invoice = new Invoice();
		invoice.setAccount(this);
		invoice.setAmount(amount + balance);
		return invoice;
	}

	public Account update(Account currAccount) {
		this.balance = currAccount.balance;
		this.isActive = currAccount.isActive;
		return this;
	}

	public Account credit(float amount) {
		balance -= amount;
		return this;
	}
}

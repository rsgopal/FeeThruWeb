package com.janakan.feethru.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notification {
	private Invoice invoice;
	private String subject;
	private String message;

	public Notification(Invoice invoice) {
		this.invoice = invoice;
		this.subject = "Dance Fee Reminder - $" + invoice.getAmount();
		this.message = "This is a friendly reminder and thanks in advance for your timely payment.";
	}
}

package com.telstra.message;

/**
 * Telstra Final Interview Case Study Project
 * @author James Koungoulos
 */

import java.util.Date;

// Message Class for the Message Cache
// Each Message contains a message string and TTL date
public class Message {
	
	private String message;
	private Date TTL;
	
	public Message(String message, Date TTL) {
		this.message = message;
		this.TTL = TTL;
	}

	public String getMessage() {
		return this.message;
	}
	
	public Date getTTL() {
		return this.TTL;
	}
}

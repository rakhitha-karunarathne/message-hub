package com.raka.messagehub.service;

import java.util.ArrayList;

import com.raka.messagehub.dto.Message;

import lombok.Getter;
import lombok.Setter;

public class MessageQueue extends ArrayList<Message>{
	
	@Getter
	@Setter
	String ownerId;
	
	@Getter
	@Setter
	String queueId;	
	
	@Getter
	long lastUsed = System.currentTimeMillis();
	public void updateTimestamp() {
		lastUsed = System.currentTimeMillis();
	}
}

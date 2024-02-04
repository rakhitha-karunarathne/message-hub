package com.raka.messagehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raka.messagehub.dto.GetMessageResponse;
import com.raka.messagehub.dto.GetQueuesResponse;
import com.raka.messagehub.dto.Message;
import com.raka.messagehub.dto.PostMessageResponse;
import com.raka.messagehub.service.MessageQueueService;

@RestController
@RequestMapping("queues/{queueOwner}")
public class QueueController {
	
	@Autowired
	MessageQueueService queueService;
	
	@GetMapping(value="message-template")
	public Message getMessageTemplate(@PathVariable String queueOwner) {
		return new Message();
	}
	
	@GetMapping(value="queues")
	public GetQueuesResponse getQueues(@PathVariable String queueOwner) {
		return queueService.getQueueIds(queueOwner);
	}	
	
	@PostMapping(value = "{queueId}/send")	
	public PostMessageResponse postMessage(@PathVariable String queueOwner, @PathVariable String queueId, @RequestBody Message m) {
		PostMessageResponse r = new PostMessageResponse();
		r.setSuccess(queueService.postMessage(queueOwner, queueId, m));
		return r;
	}
	
	@PostMapping(value = "{queueId}/receive")
	public GetMessageResponse getMessages(@PathVariable String queueOwner,@PathVariable String queueId){
		GetMessageResponse response = new GetMessageResponse();
		response.setMessages(queueService.getMessages(queueOwner,queueId));
		return response;
	}
}

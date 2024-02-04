package com.raka.messagehub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.raka.messagehub.dto.GetQueuesResponse;
import com.raka.messagehub.dto.Message;

@Service
public class MessageQueueService {
	static Logger LOGGER = Logger.getLogger(MessageQueueService.class.getName());
	long ttl = 60000;
	long timeout = 20000;

	ConcurrentHashMap<String,MessageQueue> queues = new ConcurrentHashMap<>();
	ConcurrentHashMap<String,List<MessageQueue>> owner = new ConcurrentHashMap<>();
	
	
	public GetQueuesResponse getQueueIds(String ownerId){
		GetQueuesResponse r = new GetQueuesResponse();
		owner.computeIfPresent(ownerId, (k,v)->{
			for (MessageQueue messageQueue : v) {
				r.getQueueIds().add(messageQueue.getQueueId());
			}
			return v;
		});
		return r;
	}
	public boolean postMessage(String queueOwner, String queueId, Message m) {
		String id = queueOwner + "/" + queueId;
		MessageQueue queue = queues.computeIfPresent(id, (k,v) ->{
			return v;
		});
		
		if (queue != null)
		{
			synchronized (queue) {			
				queue.updateTimestamp();
				queue.add(m);		
				queue.notifyAll();
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public List<Message> getMessages(String queueOwner,String queueId){
		String id = queueOwner + "/" + queueId;
		ArrayList<Message> messages = new ArrayList<Message>();

		
		MessageQueue queue = queues.compute(id, (k,v)->{
			if(v != null) {
				v.updateTimestamp();
				return v;
			}
			else {
				v = new MessageQueue();
				v.setOwnerId(queueOwner);
				v.setQueueId(queueId);
				getOwnerList(queueOwner).add(v);
				return v;
			}
		});
		
		synchronized (queue) {
			try {
				if(queue.size() == 0)
					queue.wait(timeout);
				
				messages.addAll(queue);
				queue.clear();
			} catch (InterruptedException e) {
				LOGGER.info("Timeout for " + id);
			}
		}
		
		return messages;
	}
	
	private List<MessageQueue> getOwnerList(String ownerId){
		return owner.compute(ownerId, (k,v)->{
			if(v != null)
				return v;
			else
				return new ArrayList<>();
		});
	}
	
	private void removeFromOwner(MessageQueue q) {
		owner.computeIfPresent(q.getOwnerId(), (k,v)->{
			v.remove(q);
			if(v.size() == 0)
				return null;
			else
				return v;
		});
	}
	
	@Scheduled(fixedDelay = 60000)
	public void purge() {
		long time = System.currentTimeMillis() - ttl;
		
		String[] keys = queues.keySet().toArray(new String[] {});
		for (String k : keys) {
			queues.computeIfPresent(k, (key,v) ->{
				if(v.getLastUsed() < time)
				{
					LOGGER.info("Purging " + key);
					removeFromOwner(v);
					return null;
				}
				else
					return v;
			});
		}
	}
}

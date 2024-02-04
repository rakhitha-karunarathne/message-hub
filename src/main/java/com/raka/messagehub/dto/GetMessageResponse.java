package com.raka.messagehub.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class GetMessageResponse extends QueueResponse{
	@Getter
	@Setter
	List<Message> messages;
}

package com.raka.messagehub.dto;

import lombok.Getter;
import lombok.Setter;

public class PostMessageResponse  extends QueueResponse{
	@Getter
	@Setter
	boolean success;
}

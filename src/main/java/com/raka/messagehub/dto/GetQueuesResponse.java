package com.raka.messagehub.dto;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class GetQueuesResponse  extends QueueResponse{
	@Getter
	@Setter
	ArrayList<String> queueIds = new ArrayList<>();
}

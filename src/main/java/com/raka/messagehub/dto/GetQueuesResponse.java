package com.raka.messagehub.dto;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class GetQueuesResponse {
	@Getter
	@Setter
	ArrayList<String> queueIds = new ArrayList<>();
}

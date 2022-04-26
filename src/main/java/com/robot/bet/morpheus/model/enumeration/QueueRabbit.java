package com.robot.bet.morpheus.model.enumeration;

import lombok.Getter;

public enum QueueRabbit {
	
	QUEUE_ZION("QueueZion");
	
	@Getter
	private String descricao;
	
	private QueueRabbit(String descricao) {
		this.descricao = descricao;
	}
	
}

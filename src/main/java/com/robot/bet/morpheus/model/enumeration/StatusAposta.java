package com.robot.bet.morpheus.model.enumeration;

import lombok.Getter;
import lombok.Setter;

public enum StatusAposta {
	
	PENDENTE         ("Pendente"), 
	PROCESSADA       ("Processada"), 
	SEM_SALDO        ("Sem Saldo"), 
	FALHOU           ("Falhou"),
	COTACAO_ALTERADA ("Cotação Alterada"),
	;
	
	@Getter @Setter
	private String descricao;
	
	private StatusAposta(String descricao) {
		this.descricao = descricao;
	}
	
}

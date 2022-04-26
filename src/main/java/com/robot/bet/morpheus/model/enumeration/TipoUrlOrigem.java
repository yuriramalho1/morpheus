package com.robot.bet.morpheus.model.enumeration;

import lombok.Getter;
import lombok.Setter;

public enum TipoUrlOrigem {

	BETSBOLA("BetsBola", "betsbola.com")
	;
	
	@Getter @Setter
	private String descricao;
	
	@Getter @Setter
	private String url;
	
	private TipoUrlOrigem(String descricao, String url) {
		this.descricao = descricao;
		this.url = url;
	}
	
	
}

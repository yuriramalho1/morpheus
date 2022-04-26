package com.robot.bet.morpheus.entity;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.robot.bet.morpheus.model.enumeration.TipoUrlOrigem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

	private Long id;
	
	private String login;
	
	private String senha;
	
	@SerializedName("tipo_url_origem")
	private TipoUrlOrigem tipoUrlOrigem;
	
	@SerializedName("data_cadastro")
	private Date dataCadastro;
	
	private Boolean ativo; 
	
}

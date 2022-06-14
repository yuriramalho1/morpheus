package com.robot.bet.morpheus.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.robot.bet.morpheus.model.enumeration.StatusAposta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aposta {

	private Long id;
	
	@SerializedName("id_campeonato")
	private Long idCampeonato;

	private String campeonato;
	
	@SerializedName("id_partida")
	private Long idPartida;
	
	@SerializedName("time_casa")
	private String timeCasa;
	
	@SerializedName("time_fora")
	private String timeFora;
	
	private String tipo;
	
	private String opcao;
	
	@SerializedName("id_opcao")
	private Long idOpcao;
	
	private BigDecimal cotacao;
	
	@SerializedName("valor_aposta")
	private BigDecimal valorAposta;
	
	@SerializedName("data_evento")
	private Date dataEvento;
	
	@SerializedName("status_aposta")
	private StatusAposta statusAposta;
	
	private Usuario usuario;
	
	@SerializedName("message_error")
	private String messageError;
	
	private BigDecimal valorSaldo;

	@Override
	public String toString() {
		return "Aposta [id=" + id + ", idCampeonato=" + idCampeonato + ", campeonato=" + campeonato + ", idPartida="
				+ idPartida + ", timeCasa=" + timeCasa + ", timeFora=" + timeFora + ", tipo=" + tipo + ", opcao="
				+ opcao + ", idOpcao=" + idOpcao + ", cotacao=" + cotacao + ", valorAposta=" + valorAposta
				+ ", dataEvento=" + dataEvento + ", statusAposta=" + statusAposta + ", usuario=" + usuario
				+ ", messageError=" + messageError + ", valorSaldo=" + valorSaldo + "]";
	}
	
}

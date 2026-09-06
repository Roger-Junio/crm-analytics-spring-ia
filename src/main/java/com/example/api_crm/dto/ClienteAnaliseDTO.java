package com.example.api_crm.dto;

import java.math.BigDecimal;

import com.example.api_crm.model.Cliente;

public class ClienteAnaliseDTO {  

    private String nome;
    private BigDecimal ticketMedio;
    private Long diasSemComprar;
    private String classificacao;


    public ClienteAnaliseDTO(String nome, BigDecimal ticketMedio, Long diasSemComprar, String classificacao) {
        this.nome = nome;
        this.ticketMedio = ticketMedio;
        this.diasSemComprar = diasSemComprar;
        this.classificacao = classificacao;
    }

    public String getNome() {return nome;}
    public BigDecimal getTicketMedio() {return ticketMedio;}
    public Long getDiasSemComprar() {return diasSemComprar;}
    public String getClassificacao() {return classificacao;}

}

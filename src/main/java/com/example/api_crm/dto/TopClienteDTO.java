package com.example.api_crm.dto;

import java.math.BigDecimal;

public class TopClienteDTO {

    private String matricula;
    private String nome;
    private String email;
    private int quantidadeCompras;
    private BigDecimal valorTotalCompras;
    private BigDecimal ticketMedio; 


    public TopClienteDTO( String matricula, String nome, String email,
        int quantidadeCompras, BigDecimal valorTotalCompras, BigDecimal ticketMedio) {

            this.matricula = matricula;
            this.nome = nome;
            this.email = email;
            this.quantidadeCompras = quantidadeCompras;
            this.valorTotalCompras = valorTotalCompras;
            this.ticketMedio = ticketMedio;
    }


    public String getMatricula() {return matricula;}
    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public int getQuantidadeCompras() {return quantidadeCompras;}
    public BigDecimal getValorTotalCompras() {return valorTotalCompras;}
    public BigDecimal getTicketMedio() {return ticketMedio; }


}

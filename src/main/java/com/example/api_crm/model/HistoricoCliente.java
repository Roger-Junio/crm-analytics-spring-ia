package com.example.api_crm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity 
public class HistoricoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "arquivo_id", nullable = false)
    private Arquivo arquivo;        


    private int quantidadeCompras;
    private BigDecimal valorTotalCompras;
    private BigDecimal ticketMedio;
    private LocalDate ultimaCompra;


    public void setCliente(Cliente cliente) {this.cliente = cliente;}
        public Cliente getCliente() {return cliente;}

    public void setQuantidadeCompras(int quantidadeCompras) {this.quantidadeCompras = quantidadeCompras;}
        public int getQuantidadeCompras() {return quantidadeCompras;}    

    public void setValorTotalCompras(BigDecimal valorTotalCompras) {this.valorTotalCompras = valorTotalCompras;}
        public BigDecimal getValorTotalCompras() {return valorTotalCompras;} 

    public void setTicketMedio(BigDecimal ticketMedio) {this.ticketMedio = ticketMedio;}
        public BigDecimal getTicketMedio() {return ticketMedio;}     
    
    public void setUltimaCompra(LocalDate ultimaCompra) {this.ultimaCompra = ultimaCompra;}
        public LocalDate getUltimaCompra() {return ultimaCompra;}


    public void setArquivo(Arquivo arquivo) {this.arquivo = arquivo;}
        public Arquivo getArquivo() {return arquivo;}    
        


    }

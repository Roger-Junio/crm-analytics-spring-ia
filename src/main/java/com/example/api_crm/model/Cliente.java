package com.example.api_crm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.api_crm.model.Cliente;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cidade;
    private String estado;
    private int quantidadeCompras;
    private BigDecimal valorTotalCompras;
    private BigDecimal ticketMedio;
    private LocalDate ultimaCompra;


    public void setId(Long id) {this.id = id; }
        public Long getId() {return id;}

    public void setNome(String nome) {this.nome = nome; }
        public String getNome() {return nome;}
     
    public void setEmail(String email) {this.email = email; }
        public String getEmail() {return email;}    

    public void setCidade(String cidade) {this.cidade = cidade;}
        public String getCidade() {return cidade;}
       
    public void setEstado(String estado) {this.estado = estado;}
        public String getEstado() {return estado;}    

    public void setQuantidadeCompras(int quantidadeCompras) { this.quantidadeCompras = quantidadeCompras;}
        public int getQuantidadeCompras() {return quantidadeCompras;}

    public void setValorTotalCompras(BigDecimal valorTotalComrpas) {this.valorTotalCompras = valorTotalComrpas;}    
        public BigDecimal getValorTotalCompras() {return valorTotalCompras;}
    
    public void setTicketMedio(BigDecimal ticketMedio) {this.ticketMedio = ticketMedio;}
        public BigDecimal getTicketMedio() { return ticketMedio;}

    public void setUltimaCompra(LocalDate ultimaCompra) {this.ultimaCompra = ultimaCompra;}
        public LocalDate getUltimaCompra() {return ultimaCompra;}
    

        

}

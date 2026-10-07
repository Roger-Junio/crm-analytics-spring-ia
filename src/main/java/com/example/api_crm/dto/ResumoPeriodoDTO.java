package com.example.api_crm.dto;

import java.math.BigDecimal;

public class ResumoPeriodoDTO {

private Integer totalClientes;
private Integer totalCompras;
private BigDecimal totalFaturamento;
private BigDecimal ticketMedioGeral;
   
    public ResumoPeriodoDTO(Integer totalClientes, Integer totalCompras, 
        BigDecimal totalFaturamento, BigDecimal ticketMedioGeral) {
            this.totalClientes = totalClientes;
            this.totalCompras = totalCompras;
            this.totalFaturamento = totalFaturamento;
            this.ticketMedioGeral = ticketMedioGeral; 
    }  
    
    public void setTotalClientes(Integer totalClientes) {this.totalClientes = totalClientes;}
        public Integer getTotalClientes() {return totalClientes;}

    public void setTotalCompras(Integer totalCompras) {this.totalCompras = totalCompras;}
        public Integer getTotalCompras() {return totalCompras;}

    public void setTotalFaturamento(BigDecimal totalFaturamento) {this.totalFaturamento = totalFaturamento;}
        public BigDecimal getTotalFaturamento() {return totalFaturamento;}

    public void setTicketMedioGeral(BigDecimal ticketMedioGeral) {this.ticketMedioGeral = ticketMedioGeral;}
        public BigDecimal getTicketMedioGeral() {return ticketMedioGeral;}
}

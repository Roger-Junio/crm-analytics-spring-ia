package com.example.api_crm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AnaliseRequestDTO {

   
    private List<Long> arquivos;

    @Min(value = 1, message = "O Limite deve ser maior que zero")
    private Integer limite;  

    @Min(value = 0, message = "O número de dias deve ser maior ou igual a zero")
    private Integer dias;

    private List<Long> arquivosPeriodoA; 
    private List<Long> arquivosPeriodoB;
   

    public void setArquivos(List<Long> arquivos) {this.arquivos = arquivos;}
        public List<Long> getArquivos() {return arquivos;}

    public void setLimite(Integer limite) {this.limite = limite;}
        public Integer getLimite() {return limite;}

    public void setDias(Integer dias) {this.dias = dias;}
        public Integer getDias() {return dias;} 
        
    public void setArquivosPeriodoA(List<Long> arquivosPeriodoA) {this.arquivosPeriodoA = arquivosPeriodoA; }
        public List<Long> getArquivosPeriodoA() {return arquivosPeriodoA; }  

    public void setArquivosPeriodoB(List<Long> arquivosPeriodoB) {this.arquivosPeriodoB = arquivosPeriodoB; }
        public List<Long> getArquivosPeriodoB() {return arquivosPeriodoB; }    


   
}

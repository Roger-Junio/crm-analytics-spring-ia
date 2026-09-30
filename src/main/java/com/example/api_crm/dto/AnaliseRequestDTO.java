package com.example.api_crm.dto;

import java.util.List;

public class AnaliseRequestDTO {

    private List<Long> arquivos;
    private Integer limite;  
    private Integer dias;
   

    public void setArquivos(List<Long> arquivos) {this.arquivos = arquivos;}
        public List<Long> getArquivos() {return arquivos;}

    public void setLimite(Integer limite) {this.limite = limite;}
        public Integer getLimite() {return limite;}

    public void setDias(Integer dias) {this.dias = dias;}
        public Integer getDias() {return dias;}    

   
}

package com.example.api_crm.dto;

import java.util.List;

public class AnaliseRequestDTO {

    private List<Long> arquivos;
    private Integer limite;  

    public void setArquivos(List<Long> arquivos) {this.arquivos = arquivos;}
        public List<Long> getArquivos() {return arquivos;}

    public void setLimite(Integer limite) {this.limite = limite;}
        public Integer getLimite() {return limite;}

   
}

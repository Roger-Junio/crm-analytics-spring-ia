package com.example.api_crm.dto;

public class ComparacaoPeriodosDTO  {
    
    private String matricula;
    private String nome;
    private Integer quantidadeDeComprasA; 
    private Integer quantidadeDeComprasB; 
    private Integer diferencaQuantidadeCompras; 


    public ComparacaoPeriodosDTO(String matricula, String nome, Integer quantidadeDeComprasA, 
        Integer quantidadeDeComprasB, Integer diferencaQuantidadeCompras) {

            this.matricula = matricula;
            this.nome = nome; 
            this.quantidadeDeComprasA = quantidadeDeComprasA; 
            this.quantidadeDeComprasB = quantidadeDeComprasB; 
            this.diferencaQuantidadeCompras = diferencaQuantidadeCompras;   
    }

    public String getMatricula() {return matricula;}
    public String getNome() {return nome;}
    public Integer getQuantidadeDeComprasA() {return quantidadeDeComprasA;}
    public Integer getQuantidadeDeComprasB() {return quantidadeDeComprasB;}
    public Integer getDiferencaQuantidadeCompras() {return diferencaQuantidadeCompras;}
}

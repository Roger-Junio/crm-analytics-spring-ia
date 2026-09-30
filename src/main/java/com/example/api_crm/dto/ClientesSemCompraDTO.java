package com.example.api_crm.dto;

public class ClientesSemCompraDTO {
    

private String matricula;
private String nome;
private String email;
private Long diasSemComprar;

public ClientesSemCompraDTO( String matricula, String nome, String email, Long diasSemComprar) {
    this.matricula = matricula; 
    this.nome = nome; 
    this.email = email;
    this.diasSemComprar = diasSemComprar;
}

    public String getMatricula() {return matricula; }
    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public Long getDiasSemComprar() {return diasSemComprar;}
    

}

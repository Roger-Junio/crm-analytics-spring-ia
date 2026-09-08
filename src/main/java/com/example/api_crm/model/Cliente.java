package com.example.api_crm.model;

import jakarta.persistence.Column;
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

    @Column(unique = true, nullable = false )
    private String matricula;

    private String nome;
    private String email;
    private String cidade;
    private String estado;


    public void setId(Long id) {this.id = id; }
        public Long getId() {return id;}

    public void setMatricula(String matricula) {this.matricula = matricula;}
        public String getMatricula() {return matricula;}       



    public void setNome(String nome) {this.nome = nome; }
        public String getNome() {return nome;}
     
    public void setEmail(String email) {this.email = email; }
        public String getEmail() {return email;}    

    public void setCidade(String cidade) {this.cidade = cidade;}
        public String getCidade() {return cidade;}
       
    public void setEstado(String estado) {this.estado = estado;}
        public String getEstado() {return estado;}    

    

        

}

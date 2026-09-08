package com.example.api_crm.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataUpload;

    public void setNome(String nome) {this.nome = nome;}
        public String getNome() {return nome;}

    public void setDataUpload(LocalDate dataUpload) {this.dataUpload = dataUpload;}
        public LocalDate getDataUpload() {return dataUpload;}


}

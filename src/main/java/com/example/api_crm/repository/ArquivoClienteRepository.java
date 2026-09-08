package com.example.api_crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api_crm.model.Arquivo;
import com.example.api_crm.model.HistoricoCliente;

@Repository 
public interface ArquivoClienteRepository extends JpaRepository<Arquivo, Long> {

    
}  

 
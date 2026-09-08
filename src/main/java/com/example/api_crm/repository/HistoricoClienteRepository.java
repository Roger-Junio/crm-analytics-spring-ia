package com.example.api_crm.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.api_crm.model.HistoricoCliente;

public interface HistoricoClienteRepository extends  JpaRepository<HistoricoCliente, Long> {

}


package com.example.api_crm.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.api_crm.model.HistoricoCliente;

public interface HistoricoClienteRepository extends  JpaRepository<HistoricoCliente, Long> {

    List<HistoricoCliente> findByArquivoIdIn(List<Long> arquivos);
    List<HistoricoCliente> findByClienteId(Long clienteId);
}


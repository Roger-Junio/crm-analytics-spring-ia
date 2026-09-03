package com.example.api_crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.api_crm.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    

    


}

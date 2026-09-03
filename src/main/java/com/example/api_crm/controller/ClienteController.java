package com.example.api_crm.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.api_crm.model.Cliente;
import com.example.api_crm.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    //RECEBEDOR DO ARQUIVO CSV
    @PostMapping("/importar")
    public void importar(@RequestParam("arquivo") MultipartFile arquivo)
            throws IOException {
        clienteService.importarArquivo(arquivo);
    }

    //BUSCA PELA ID DO CLIENTE PARA SABER A QUANTIDADES DE DIAS SEM COMPRA
    //PARA TESTE POSTMAN
    @GetMapping("/{id}/dias-sem-compra")
    public Long buscarTeste(@PathVariable Long id) {
        return clienteService.diasSemComprar(id);
    }

   
}

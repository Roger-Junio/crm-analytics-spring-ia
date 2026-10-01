package com.example.api_crm.controller;

import java.io.IOException;
import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.api_crm.dto.AnaliseRequestDTO;
import com.example.api_crm.dto.ClienteAnaliseDTO;
import com.example.api_crm.dto.ClientesSemCompraDTO;
import com.example.api_crm.model.HistoricoCliente;
import com.example.api_crm.service.ClienteService;
import com.example.api_crm.dto.TopClienteDTO;

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
    //RECEBE UM ID 
    @GetMapping("/{id}/buscando-historico-pelo-id-cliente")
    public List<HistoricoCliente> getMethodName(@PathVariable Long id) {
        return clienteService.dadosArquivoHistoricoCliente(id);
    }

    //RECEBE UM ID 
    //BUSCA CONSOLIDADA ESPECIFICA DE UM ID CLIENTE
    @GetMapping("/{id}/buscando-dados-consolidados")
    public ClienteAnaliseDTO buscarDadosConsolidadosDTO(@PathVariable Long id) {
        return clienteService.buscarDadosConsolidados(id);
    }

    //RECEBE UM DTO/FILTRO
    //CLIENTES EM RISCO
    @PostMapping("/analise/clientes-em-risco")
    public List<ClienteAnaliseDTO> buscarClientesEmRisco(@RequestBody AnaliseRequestDTO request) {
        return clienteService.buscarClientesEmRisco(request);
    }

    //RECEBE UM DTO/FILTRO 
    //TOP CLIENTES QUE + TEVE NUMEROS DE COMPRA
    @PostMapping("/analise/top-clientes-limite")
    public List<TopClienteDTO> buscarTopClientesMaisCompras(@RequestBody AnaliseRequestDTO request) {
    return clienteService.buscarTopClientes(request);
    }

    //RECEBE UM DTO/FILTRO 
    //TOP CLIENTES QUE + TEVE VALOR COMPRADO 
    @PostMapping("/analise/top-clientes-limite-valor-comprado")
    public List<TopClienteDTO> buscarTopClientesLimiteValorComprado(@RequestBody AnaliseRequestDTO request) {
    return clienteService.buscarTopClientesPorValorComprado(request);
    }

    //TOP CLIENTES X DIAS SEM COMPRAS
    @PostMapping("/analise/top-clientes-sem-compra-x-dias")
    public List<ClientesSemCompraDTO> topClientesSemCompraAXDias(@RequestBody AnaliseRequestDTO request) {
        return clienteService.topClientesSemCompraAXDias(request);
    }

    

  
    




    

}
   


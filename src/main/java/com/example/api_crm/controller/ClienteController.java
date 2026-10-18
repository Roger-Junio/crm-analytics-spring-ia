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

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.example.api_crm.dto.TopClienteDTO;
import com.example.api_crm.dto.ComparacaoPeriodosDTO;
import com.example.api_crm.dto.ResumoPeriodoDTO;


@Tag(
    name = "CRM Analytics — Clientes",
    description = "Importação de arquivos CSV e análises de clientes"
)

    @RestController
    @RequestMapping("/clientes")
    public class ClienteController {

        private final ClienteService clienteService;

        public ClienteController(ClienteService clienteService) {
            this.clienteService = clienteService;
        }

    @Operation(
        summary = "Importar arquivo CSV",
        description = "Recebe um arquivo CSV com dados de clientes e realiza sua importação para o sistema."
    )
            @PostMapping("/importacoes")
            public void importar(@RequestParam("arquivo") MultipartFile arquivo)
                    throws IOException {
                clienteService.importarArquivo(arquivo);
            }


    @Operation(
        summary = "Consultar dias sem compra",
        description = "Consulta a quantidade de dias sem compra de um cliente a partir do seu ID."
    )   
            @GetMapping("/{id}/dias-sem-compra")
            public Long buscarTeste(@PathVariable Long id) {
                return clienteService.diasSemComprar(id);
            }


    @Operation(
        summary = "Consultar histórico do cliente",
        description = "Retorna os registros de histórico de compras associados ao ID informado."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @GetMapping("/{id}/historicos")
            public List<HistoricoCliente> getMethodName(@PathVariable Long id) {
                return clienteService.dadosArquivoHistoricoCliente(id);
            }

            
    @Operation(
        summary = "Consultar resumo consolidado do cliente",
        description = "Retorna informações consolidadas do histórico de um cliente, incluindo ticket médio, dias sem compra e classificação."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @GetMapping("/{id}/resumo")
            public ClienteAnaliseDTO buscarDadosConsolidadosDTO(@PathVariable Long id) {
                return clienteService.buscarDadosConsolidados(id);
            }


    @Operation(
        summary = "Buscar clientes em risco",
        description = "Analisa o histórico de compras dos arquivos selecionados e classifica os clientes conforme o tempo sem comprar."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/clientes-em-risco")
            public List<ClienteAnaliseDTO> buscarClientesEmRisco(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.buscarClientesEmRisco(request);
            }


    @Operation(
        summary = "Consultar clientes com mais compras",
        description = "Retorna os clientes com maior quantidade de compras nos arquivos selecionados."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/top-quantidade-compras")
            public List<TopClienteDTO> buscarTopClientesMaisCompras(@Valid @RequestBody AnaliseRequestDTO request) {
            return clienteService.buscarTopClientes(request);
            }


    @Operation(
        summary = "Consultar clientes com maior valor em compras",
        description = "Retorna os clientes classificados pelo valor total de compras nos arquivos selecionados."
        )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})    
            @PostMapping("/analises/top-valor-compras")
            public List<TopClienteDTO> buscarTopClientesLimiteValorComprado(@Valid @RequestBody AnaliseRequestDTO request) {
            return clienteService.buscarTopClientesPorValorComprado(request);
            }


    @Operation(
        summary = "Consultar clientes sem comprar há X dias",
        description = "Identifica clientes que estão sem realizar compras pelo período informado na solicitação."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/sem-compra")
            public List<ClientesSemCompraDTO> topClientesSemCompraAXDias(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.topClientesSemCompraAXDias(request);
            }


    @Operation(
        summary = "Consultar clientes com maior ticket médio",
        description = "Classifica os clientes pelo ticket médio calculado a partir dos valores e quantidades de compras."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/maior-ticket-medio")
            public List<TopClienteDTO> buscaClientesTicketMedio(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.topClientesTicketMedio(request);
            }


    @Operation(
        summary = "Comparar períodos de compras",
        description = "Compara os resultados de compras dos clientes entre dois conjuntos de arquivos selecionados."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/comparacao-periodos")
            public List<ComparacaoPeriodosDTO> comparacaoEntrePeriodos(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.comparacaoEntrePeriodos(request);
            }


    @Operation(
        summary = "Identificar clientes com queda nas compras",
        description = "Compara dois períodos e retorna os clientes que apresentaram queda nas compras."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/queda-compras")
            public List<ComparacaoPeriodosDTO> quedaDeCompras(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.quedaDeCompras(request);
            }

            
    @Operation(
        summary = "Consultar resumo dos períodos selecionados",
        description = "Apresenta um resumo consolidado dos dados de compras dos arquivos selecionados."
    )
    @ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Cliente não encontrado"
    )
})
            @PostMapping("/analises/resumo-periodos")
            public ResumoPeriodoDTO resumoPeriodo(@Valid @RequestBody AnaliseRequestDTO request) {
                return clienteService.resumoPeriodo(request);
            }

}



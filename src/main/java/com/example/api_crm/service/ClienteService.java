package com.example.api_crm.service;

import com.example.api_crm.dto.AnaliseRequestDTO;
import com.example.api_crm.dto.ClienteAnaliseDTO;
import com.example.api_crm.model.Arquivo;
import com.example.api_crm.model.Cliente;
import com.example.api_crm.model.HistoricoCliente;
import com.example.api_crm.repository.ArquivoClienteRepository;
import com.example.api_crm.repository.ClienteRepository;
import com.example.api_crm.repository.HistoricoClienteRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final HistoricoClienteRepository historicoClienteRepository;
    private final ArquivoClienteRepository arquivoClienteRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            HistoricoClienteRepository historicoClienteRepository,
            ArquivoClienteRepository arquivoClienteRepository) {

        this.clienteRepository = clienteRepository;
        this.historicoClienteRepository = historicoClienteRepository;
        this.arquivoClienteRepository = arquivoClienteRepository;
    }
    
        //calcula o ticket medio
        public BigDecimal ticketMedioHistorico(HistoricoCliente historicoCliente) {

                var quantidadeCompras = historicoCliente.getQuantidadeCompras();
                var valorTotalCompras = historicoCliente.getValorTotalCompras();

                        if (quantidadeCompras == 0) {
                                return BigDecimal.ZERO;
                        }
                        
                return valorTotalCompras.divide(
                BigDecimal.valueOf(quantidadeCompras),
                2,
                RoundingMode.HALF_UP
                );
        }
   
        // CALCULA QUANTOS DIAS DESDE A ÚLTIMA COMPRA
        public Long diasDesdeUltimaCompra(HistoricoCliente historicoCliente) {

                LocalDate hoje = LocalDate.now();
                LocalDate ultimaCompra = historicoCliente.getUltimaCompra();

                return ChronoUnit.DAYS.between(ultimaCompra, hoje);
        }
   
        // BUSCA QUANTOS DIAS O CLIENTE ESTÁ SEM COMPRAR
        public Long diasSemComprar(Long id) {

                var resultado = historicoClienteRepository.findById(id).orElseThrow();
                return diasDesdeUltimaCompra(resultado);
        }
    
        //CLASIFICAÇÃO DO CLIENTE
        public String classificarCliente(HistoricoCliente historicoCliente) {

                long dias = diasDesdeUltimaCompra(historicoCliente);

                if (dias <= 30) {
                return "Ativo";

                } else if (dias <= 60) {
                return "Atenção";

                } else {
                return "Em risco";
                }
        }

        //IMPORTA ARQUIVO CSV
        public void importarArquivo(MultipartFile arquivo) throws IOException {      
        //BufferedReader utilizado para leitura do arquivo //InputStreamReader utliza para transforma byst em char para melhor utilizar
        BufferedReader reader = new BufferedReader( new InputStreamReader(arquivo.getInputStream()));    
        String linha;
        reader.readLine();

                //SALVANDO NA TABELA ARQUIVO, O NOME DO ARQUIVO E A DATA DO UPLOAD
                Arquivo arquivoImportado = new Arquivo();
                arquivoImportado.setNome(arquivo.getOriginalFilename());
                arquivoImportado.setDataUpload(LocalDate.now());
                        arquivoClienteRepository.save(arquivoImportado);


        while ((linha = reader.readLine()) != null) {

            String[] colunas = linha.split(";");

                String matricula = colunas[0];
                String nome = colunas[1];
                String email = colunas[2];
                String cidade = colunas[3];
                String estado = colunas[4];


            //DADOS DO HISTORICO
            int quantidadeCompras = Integer.parseInt(colunas[5]);
            BigDecimal valorTotalCompras = new BigDecimal(colunas[6]);
            LocalDate ultimaCompra = LocalDate.parse(colunas[7]);

            //PROCURA CLIENTE PELA MATRICULA    
            Optional<Cliente> clienteExistente =
                    clienteRepository.findByMatricula(matricula);

            Cliente cliente;

            // IF CLIENTE JÁ EXISTE
            // Pegamos o cliente existente e atualizamos
            // seus dados cadastrais caso tenham mudado.      
            if (clienteExistente.isPresent()) {

                cliente = clienteExistente.get();

                cliente.setNome(nome);
                cliente.setEmail(email);
                cliente.setCidade(cidade);
                cliente.setEstado(estado);

                clienteRepository.save(cliente);
            }
      
            // CLIENTE NÃO EXISTE
            // Criamos um novo Cliente utilizando a matrícula como identificador do cliente.          
            else {

                cliente = new Cliente();

                cliente.setMatricula(matricula);
                cliente.setNome(nome);
                cliente.setEmail(email);
                cliente.setCidade(cidade);
                cliente.setEstado(estado);

                clienteRepository.save(cliente);
            }


            // CRIA O HISTÓRICO DO CLIENTE
            // ====================================================
            // alteração:
            // Este bloco ficara FORA do if/else.
            // Tanto cliente novo e quanto cliente existente
            // precisam receber um novo histórico referente ao arquivo que acabou de ser importado.
         

            HistoricoCliente historicoCliente = new HistoricoCliente();

            historicoCliente.setCliente(cliente);
            historicoCliente.setArquivo(arquivoImportado);
            historicoCliente.setQuantidadeCompras(quantidadeCompras);
            historicoCliente.setValorTotalCompras(valorTotalCompras);

            historicoCliente.setUltimaCompra(ultimaCompra);

                // Calcula automaticamente o ticket médio
                historicoCliente.setTicketMedio(
                        ticketMedioHistorico(historicoCliente)
                );

            historicoClienteRepository.save(historicoCliente);
        }
    }
     
        public List<HistoricoCliente> dadosArquivoHistoricoCliente(Long id) {

                var resultadoHistoricoId = historicoClienteRepository.findByClienteId(id);
                return resultadoHistoricoId; 
        }
       
      


        //buscarDadosConsolidados() → busca os dados___________________________________________________________________________________________
        //consolidarHistorico() → processa/análise os dados

                public ClienteAnaliseDTO buscarDadosConsolidados(Long id) {
                        List<HistoricoCliente> historico = historicoClienteRepository.findByClienteId(id);
                        return consolidarHistorico(historico);
                }

                public ClienteAnaliseDTO consolidarHistorico(List<HistoricoCliente> historicos) {

                LocalDate hoje = LocalDate.now();
                LocalDate ultimaCompraConsolidada = null;

                int quantidadeComprasConsolidada = 0;
                BigDecimal valorTotalComprasConsolidada = BigDecimal.ZERO;

                        // esse for é a mágica completa
                        for (HistoricoCliente historico : historicos) {

                                quantidadeComprasConsolidada =
                                quantidadeComprasConsolidada + historico.getQuantidadeCompras();

                                valorTotalComprasConsolidada =
                                valorTotalComprasConsolidada.add(
                                        historico.getValorTotalCompras()
                                );

                                LocalDate ultimaCompra = historico.getUltimaCompra();

                                if (ultimaCompraConsolidada == null) {
                                ultimaCompraConsolidada = ultimaCompra;

                                } else if (ultimaCompra.isAfter(ultimaCompraConsolidada)) {
                                ultimaCompraConsolidada = ultimaCompra;
                                }
                        }
                
                                // Calcula o ticket médio considerando todo o histórico
                                BigDecimal ticketMedioConsolidado =
                                        valorTotalComprasConsolidada.divide(
                                                BigDecimal.valueOf(quantidadeComprasConsolidada),
                                                2,
                                                RoundingMode.HALF_UP
                                        );

                                // Calcula quantos dias se passaram desde a última compra
                                Long diasSemComprar =
                                        ChronoUnit.DAYS.between(
                                                ultimaCompraConsolidada,
                                                hoje
                                        );

                                String classificacao; 
                                if (diasSemComprar <= 30) {
                                        classificacao = "Ativo";
                                }   else if (diasSemComprar <= 60) {
                                        classificacao = "Atencao";
                                }   else {
                                        classificacao = "Em risco"; 
                                }

                                return new ClienteAnaliseDTO(
                                        historicos.get(0).getCliente().getNome(),
                                        ticketMedioConsolidado,
                                        diasSemComprar,
                                        classificacao
                                );

                
                }
        
         //________________________________________________________________________________________________________________________________________



}
        






               

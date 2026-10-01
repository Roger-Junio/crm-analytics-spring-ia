package com.example.api_crm.service;

import com.example.api_crm.dto.AnaliseRequestDTO;
import com.example.api_crm.dto.ClienteAnaliseDTO;
import com.example.api_crm.dto.ClientesSemCompraDTO;
import com.example.api_crm.dto.TopClienteDTO;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
        //calcula o ticket medio_______________________________________________________________________________________________________________________________
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
        }//___________________________________________________________________________________________________________________________________________________     
   
        // CALCULA QUANTOS DIAS DESDE A ÚLTIMA COMPRA_______________________________________________________________________________________________________________________________
        public Long diasDesdeUltimaCompra(HistoricoCliente historicoCliente) {

                LocalDate hoje = LocalDate.now();
                LocalDate ultimaCompra = historicoCliente.getUltimaCompra();

                return ChronoUnit.DAYS.between(ultimaCompra, hoje);
        }//______________________________________________________________________________________________________________________________________________________________________________
    
        // BUSCA QUANTOS DIAS O CLIENTE ESTÁ SEM COMPRAR//______________________________________________________________________________________________________________________________________________________________________________
        public Long diasSemComprar(Long id) {

                var resultado = historicoClienteRepository.findById(id).orElseThrow();
                return diasDesdeUltimaCompra(resultado);
        }//______________________________________________________________________________________________________________________________________________________________________________
    
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
        }//______________________________________________________________________________________________________________________________________________________________________________

        //IMPORTA ARQUIVO CSV______________________________________________________________________________________________________________________________________________________________________________
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
       
        //Consolida Historico cliente_______________________________________________________________________________________________________________________________________
        public Map<Long, List<HistoricoCliente>> agruparHistoricosPorCliente(List<HistoricoCliente> historicos) {
                
                Map<Long, List<HistoricoCliente>> historicosPorCliente = new HashMap<>();

                for (HistoricoCliente historico : historicos) {
                        
                        Long clienteId = historico.getCliente().getId();

                        if (historicosPorCliente.containsKey(clienteId)) {
                                historicosPorCliente.get(clienteId).add(historico);     
                        } else {
                               List<HistoricoCliente> lista = new ArrayList<>();
                               lista.add(historico);
                               historicosPorCliente.put(clienteId, lista);
                        }
                }  
                
                return historicosPorCliente;
        }//______________________________________________________________________________________________________________________________________________________________________________

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
                }//________________________________________________________________________________________________________________________________________
        
        //CLIENTES EM RISCO______________________________________________________________________________________________________________________________________________________
        public List<ClienteAnaliseDTO> buscarClientesEmRisco(AnaliseRequestDTO request) {

                List<ClienteAnaliseDTO> resultados = new ArrayList<>();
                List<HistoricoCliente> historicos = historicoClienteRepository.findByArquivoIdIn(request.getArquivos());

                Map<Long, List<HistoricoCliente>> historicosPorCliente = agruparHistoricosPorCliente(historicos);

                for (Map.Entry<Long, List<HistoricoCliente>> entrada : historicosPorCliente.entrySet()) {

                        ClienteAnaliseDTO resultado = consolidarHistorico(entrada.getValue());

                        if (resultado.getClassificacao().equals("Em risco")) {
                        resultados.add(resultado);
                        }
                }
                        return resultados;
                }//_______________________________________________________________________________________________________________________________________________________________________

        //TOP CLIENTES QUE + TEVE NUMEROS DE COMPRAS______________________________________________________________________________________________________________________________
        public List<TopClienteDTO> buscarTopClientes(AnaliseRequestDTO request) {
                                
                        List<HistoricoCliente> historicos = historicoClienteRepository.findByArquivoIdIn(request.getArquivos());               
                        Map<Long, List<HistoricoCliente>> historicosPorCliente = agruparHistoricosPorCliente(historicos);
                        Map<Long, Integer> comprasPorCliente = new HashMap<>();
                        
                        for (Map.Entry<Long, List<HistoricoCliente>> entrada : historicosPorCliente.entrySet()) {
                                Long clienteId = entrada.getKey();
                                
                                List<HistoricoCliente> historicosCliente = entrada.getValue();

                                int quantidadeComprasTotal = 0;

                                        for (HistoricoCliente historico : historicosCliente) {
                                                quantidadeComprasTotal = quantidadeComprasTotal + historico.getQuantidadeCompras();
                                        }            
                                        
                                comprasPorCliente.put( clienteId, quantidadeComprasTotal);
                        } 

                        List<Map.Entry<Long, Integer>> ranking = new ArrayList<>(comprasPorCliente.entrySet());
                        
                        ranking.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                        List<Map.Entry<Long, Integer>> rankingLimitado = ranking.stream().limit(request.getLimite()).toList();;
                        
                        List<TopClienteDTO> resultados = new ArrayList<>();
                        
                                for (Map.Entry<Long, Integer> entrada : rankingLimitado) {

                                        Long clienteId = entrada.getKey();
                                        Integer quantidadeCompras = entrada.getValue();

                                        List<HistoricoCliente> historicoCliente = historicosPorCliente.get(clienteId); 

                                        BigDecimal valorTotalCompras = BigDecimal.ZERO; 

                                                for (HistoricoCliente historico : historicoCliente) {
                                                       valorTotalCompras  = valorTotalCompras.add(historico.getValorTotalCompras());
                                                }
                                                
                                                BigDecimal ticketMedio = valorTotalCompras.divide(BigDecimal.valueOf(quantidadeCompras),2,
                                        RoundingMode.HALF_UP);

                                        Cliente cliente = historicoCliente.get(0).getCliente();

                                        TopClienteDTO resultado = new TopClienteDTO(
                                                cliente.getMatricula(),
                                                cliente.getNome(),
                                                cliente.getEmail(),
                                                quantidadeCompras,
                                                valorTotalCompras,
                                                ticketMedio
                                        );

                                        resultados.add(resultado);
                                }
                                        return resultados;                                  
                }

        //TOP CLIENTES QUE + TEVE VALOR COMPRADO _________________________________________________________________________________________________________________________________
        public List<TopClienteDTO> buscarTopClientesPorValorComprado(AnaliseRequestDTO request) {

                  
                        List<HistoricoCliente> historicos = historicoClienteRepository.findByArquivoIdIn(request.getArquivos());               
                        Map<Long, List<HistoricoCliente>> historicosPorCliente = agruparHistoricosPorCliente(historicos);
                        
                        Map<Long, BigDecimal> valorComprasPorCliente = new HashMap<>();
                        
                        for (Map.Entry<Long, List<HistoricoCliente>> entrada : historicosPorCliente.entrySet()) {
                                
                                Long clienteId = entrada.getKey();
                                List<HistoricoCliente> historicosCliente = entrada.getValue();

                                //ALTERADO 
                                BigDecimal valorComprasTotal = BigDecimal.ZERO; 

                                        for (HistoricoCliente historico : historicosCliente) {
                                                valorComprasTotal = valorComprasTotal.add(historico.getValorTotalCompras());
                                        }
                                  
                                        valorComprasPorCliente.put( clienteId, valorComprasTotal);
                        } 
                 
                        List<Map.Entry<Long, BigDecimal>> ranking = new ArrayList<>(valorComprasPorCliente.entrySet());
          
                        ranking.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));              
                        List<Map.Entry<Long, BigDecimal>> rankingLimitado = ranking.stream().limit(request.getLimite()).toList();

                        List<TopClienteDTO> resultados = new ArrayList<>();
                        
                                for (Map.Entry<Long, BigDecimal> entrada : rankingLimitado) {

                                        Long clienteId = entrada.getKey();
                                        List<HistoricoCliente> historicoCliente = historicosPorCliente.get(clienteId); 

                                        BigDecimal valorTotalCompras = BigDecimal.ZERO; 
                                        int quantidadeCompras = 0;

                                                for (HistoricoCliente historico : historicoCliente) {
                                                       valorTotalCompras  = valorTotalCompras.add(historico.getValorTotalCompras());
                                                       quantidadeCompras += historico.getQuantidadeCompras();
                                                }
                                                         
                                        BigDecimal ticketMedio = valorTotalCompras.divide(BigDecimal.valueOf(quantidadeCompras),2,RoundingMode.HALF_UP);
                                        Cliente cliente = historicoCliente.get(0).getCliente();

                                        TopClienteDTO resultado = new TopClienteDTO(    
                                                cliente.getMatricula(),
                                                cliente.getNome(),
                                                cliente.getEmail(),
                                                quantidadeCompras,
                                                valorTotalCompras,
                                                ticketMedio
                                        );

                                        resultados.add(resultado);
                                }
                                        return resultados;                
                }//________________________________________________________________________________________________________________________________________________________

         //TOP CLIENTES X DIAS SEM COMPRAS________________________________________________________________________________________________________________________________________
        public List<ClientesSemCompraDTO> topClientesSemCompraAXDias(AnaliseRequestDTO request) {

        List<HistoricoCliente> historicos = historicoClienteRepository.findByArquivoIdIn(request.getArquivos());               
        Map<Long, List<HistoricoCliente>> historicosPorCliente = agruparHistoricosPorCliente(historicos);                 
                
        Map<Long, Long> diasTotalSemCompra = new HashMap<>();
                                    
                for (Map.Entry<Long, List<HistoricoCliente>> entrada : historicosPorCliente.entrySet()) {
                                                              
                                Long clienteId = entrada.getKey();
                                List<HistoricoCliente> historicoCliente = entrada.getValue();
                              
                                LocalDate ultimaCompraTotal = null; 
                          
                                for (HistoricoCliente historico : historicoCliente) {
                                               
                                               LocalDate ultimaCompra = historico.getUltimaCompra();

                                                 if (ultimaCompraTotal == null) {
                                                ultimaCompraTotal = ultimaCompra;}

                                                else if (ultimaCompra.isAfter(ultimaCompraTotal)) {
                                                        ultimaCompraTotal = ultimaCompra;  
                                                }
                                }
                                
                                diasTotalSemCompra.put(clienteId, ChronoUnit.DAYS.between(ultimaCompraTotal, LocalDate.now()));                        
                        } 
                    
                        List<Map.Entry<Long, Long>> ranking = new ArrayList<>(diasTotalSemCompra.entrySet());             
                        List<Map.Entry<Long, Long>> rankingLimitadoDias = ranking.stream().filter(entrada -> entrada.getValue() >= request.getDias()).sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
               
                        List<Map.Entry<Long, Long>> rankingLimitado = rankingLimitadoDias.stream().limit(request.getLimite()).toList();
                        
                        List<ClientesSemCompraDTO> resultados = new ArrayList<>();
                        
                                for (Map.Entry<Long, Long> entrada : rankingLimitado) {     

                                        Long clienteId = entrada.getKey(); 
                                        Long diasSemComprar = entrada.getValue();

                                        List<HistoricoCliente> historicoCliente = historicosPorCliente.get(clienteId);
                                       
                                        Cliente cliente = historicoCliente.get(0).getCliente();       
                                                                        
                                        ClientesSemCompraDTO resultado = new ClientesSemCompraDTO( 
                                                cliente.getMatricula(),
                                                cliente.getNome(),
                                                cliente.getEmail(),
                                                diasSemComprar
                                        );
                                        resultados.add(resultado);
                                }
                                return resultados;                       
                        }//_______________________________________________________________________________________________________________________________________________________________________        


        //TOP CLIENTES TICKET MEDIO________________________________________________________________________________________________________________________________
        public List<TopClienteDTO> topClientesTicketMedio(AnaliseRequestDTO request) {

        List<HistoricoCliente> historicos = historicoClienteRepository.findByArquivoIdIn(request.getArquivos());                                
        Map<Long, List<HistoricoCliente>> historicosPorCliente = agruparHistoricosPorCliente(historicos);                        
        
        Map<Long, BigDecimal> ticketMedioTodo = new HashMap<>();

                for (Map.Entry<Long, List<HistoricoCliente>> entrada : historicosPorCliente.entrySet()) {
                        
                        Long clienteId = entrada.getKey(); 
                        List<HistoricoCliente> historicoClientes = entrada.getValue(); 

                        BigDecimal valorTotalCompras  = BigDecimal.ZERO; 
                        int quantidadeCompras = 0;

                                for (HistoricoCliente historico : historicoClientes ) {
                                        
                                        valorTotalCompras  = valorTotalCompras .add(historico.getTicketMedio());
                                        quantidadeCompras += historico.getQuantidadeCompras();                
                                }

                                BigDecimal ticketMedio = valorTotalCompras .divide(BigDecimal.valueOf(quantidadeCompras),2,RoundingMode.HALF_UP);
                                ticketMedioTodo.put(clienteId, ticketMedio); 
                }

                List<Map.Entry<Long, BigDecimal>> ranking = new ArrayList<>(ticketMedioTodo.entrySet()); 
                ranking.sort(Map.Entry.comparingByValue(Comparator.reverseOrder())); 
                List<Map.Entry<Long, BigDecimal>> rankingLimitado = ranking.stream().limit(request.getLimite()).toList();
                
                        List<TopClienteDTO> resultados = new ArrayList<>();

                        for (Map.Entry<Long, BigDecimal> entrada : rankingLimitado) {

                                Long clienteId = entrada.getKey();
                                BigDecimal ticketMedio = entrada.getValue();

                                List<HistoricoCliente> historicoCliente = historicosPorCliente.get(clienteId);
                                
                                int quantidadeCompras = 0;
                                BigDecimal valorTotalCompras = BigDecimal.ZERO;

                                for ( HistoricoCliente historico : historicoCliente) {
                                        quantidadeCompras = quantidadeCompras + historico.getQuantidadeCompras(); 
                                        valorTotalCompras = valorTotalCompras.add(historico.getValorTotalCompras()); 

                                }

                                Cliente cliente = historicoCliente.get(0).getCliente();

                                        TopClienteDTO resultado = new TopClienteDTO(
                                                cliente.getMatricula(),
                                                cliente.getNome(),
                                                cliente.getEmail(),
                                                quantidadeCompras,
                                                valorTotalCompras,
                                                ticketMedio
                                        );

                                       
                                        resultados.add(resultado);  
                        }
                                return resultados;
        }//______________________________________________________________________________________________________________________________________________________________________               

}
               

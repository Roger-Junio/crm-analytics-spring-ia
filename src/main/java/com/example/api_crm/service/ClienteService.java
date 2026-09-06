package com.example.api_crm.service;

import com.example.api_crm.dto.ClienteAnaliseDTO;
import com.example.api_crm.model.Cliente;
import com.example.api_crm.repository.ClienteRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClienteService {

  private final ClienteRepository clienteRepository; 

    public ClienteService(ClienteRepository clienteRepository) {
      this.clienteRepository = clienteRepository;
    }
      
    //CALCULA AUTOMATICAMENTE O TICK MEDIO DO CLIENTE, E SALVA JUNTO AO ID NO BANCO 
    public BigDecimal ticketMedioCliente(Cliente cliente) {
      var quantidadeCompras = cliente.getQuantidadeCompras();
      var valorTotalCompras = cliente.getValorTotalCompras();
    
          if (quantidadeCompras == 0) {
            return BigDecimal.ZERO;
        }
      var ticketMedio = valorTotalCompras.divide(
        BigDecimal.valueOf(quantidadeCompras)
      );
      return ticketMedio; 
  }

    //AQUI TRAS O DADO DO DIA DA ULTIMA COMPRA É O DIA DE HOJE  "COMP 1/2"
    public Long diasDesdeUltimaCompra(Cliente cliente) {
      LocalDate hoje = LocalDate.now();
      LocalDate ultimaCompra = cliente.getUltimaCompra();
        return ChronoUnit.DAYS.between(ultimaCompra, hoje);
    }            
      //AQUI UTILIZA O DADO DO "COMP 1/2" PARA RESULTA E TRASER A QUANTIDADE DE DIA SEM COMPRA "COMP 2/2"
      public Long diasSemComprar(Long id) {      
                    var resultado = clienteRepository.findById(id).orElseThrow();
                    return diasDesdeUltimaCompra(resultado);
                }

    //STATUS DO CLIENTE      
    public String classificarCliente(Cliente cliente) {
                
                  long dias = diasDesdeUltimaCompra(cliente);
                  
                  if (dias <= 30) {
                    return "Ativo";

                  } else if (dias <= 60) {        
                    return "Atenção";

                  } else {        
                    return "Em risco";

                  }

              }


  public void importarArquivo(MultipartFile arquivo) throws IOException {
    //BufferedReader utilizado para leitura do arquivo 
    //InputStreamReader utliza para transforma byst em char para melhor utilizar 
    BufferedReader reader = new BufferedReader( new InputStreamReader(arquivo.getInputStream()));

          String linha;
          reader.readLine(); //pula o cabeçalho do arquivo csv
        while ((linha = reader.readLine()) != null) {
  
          String[] colunas = linha.split(";");
    
              String nome = colunas[0];
              String email = colunas[1];
              String cidade = colunas[2];
              String estado = colunas[3]; 
              
              int quantidadeCompras = Integer.parseInt(colunas[4]);
              BigDecimal valorTotalCompras = new BigDecimal(colunas[5]);
              LocalDate ultimaCompra = LocalDate.parse(colunas[6]);

              Cliente cliente = new  Cliente(); 

          
              cliente.setQuantidadeCompras(quantidadeCompras);
              cliente.setValorTotalCompras(valorTotalCompras);
              cliente.setUltimaCompra(ultimaCompra);
              
              cliente.setTicketMedio(ticketMedioCliente(cliente));


                clienteRepository.save(cliente);

        }
  }

  //ANALISE DADOS DO DTO
  public ClienteAnaliseDTO analisarCliente(Cliente cliente) {

    BigDecimal ticket = ticketMedioCliente(cliente);
    Long dias = diasDesdeUltimaCompra(cliente);
    String classificacao = classificarCliente(cliente);

    return new ClienteAnaliseDTO(
        cliente.getNome(),
        ticket,
        dias,
        classificacao
    );

  }       
  //RETORNO DTO 
        public ClienteAnaliseDTO analisarClientePorID(Long id){
         var cliente = clienteRepository.findById(id).orElseThrow();
          return analisarCliente(cliente);
         } 

  //RETORNO DTO BUSCA TODOS CLIENTES 
  public List<ClienteAnaliseDTO> analisarTodosOsClientes() {
    
          var clientes = clienteRepository.findAll();
          List<ClienteAnaliseDTO> analises = new ArrayList<>();

        for (Cliente cliente : clientes) {
          analises.add(analisarCliente(cliente));
        }
    
          return analises;
        }
  
}

  












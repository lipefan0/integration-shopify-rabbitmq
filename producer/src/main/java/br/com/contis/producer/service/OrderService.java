package br.com.contis.producer.service;

import br.com.contis.producer.component.OrderRequest;
import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRequest orderRequest;

    public String enviarDados(PedidoDTO order) {

        try {
            orderRequest.send(order);
        } catch (JsonProcessingException e) {
            return "Erro ao enviar o dados." + e.getMessage();
        }

        return "Dados enviados com sucesso!";
    }
}

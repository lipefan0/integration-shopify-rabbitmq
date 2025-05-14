package br.com.contis.producer.controller;

import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import br.com.contis.producer.dto.WebhookRequestDTO;
import br.com.contis.producer.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping(
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String process(@RequestParam("data") String dataJson) {
        try {
            // Desserializa o JSON que veio dentro de data=...
            WebhookPayloadDTO payload =
                    objectMapper.readValue(dataJson, WebhookPayloadDTO.class);

            PedidoDTO pedido = payload.getRetorno()
                    .getPedidos()
                    .get(0)
                    .getPedido();

            return orderService.enviarDados(pedido);

        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido", e);
        }

    }
}

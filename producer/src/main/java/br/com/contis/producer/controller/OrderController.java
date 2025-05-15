package br.com.contis.producer.controller;

import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import br.com.contis.producer.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private ObjectMapper objectMapper;

    @PostMapping(
            path = "",
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String processRaw(@RequestBody byte[] raw) throws Exception {
        // 1) Converte o corpo cru em String
        String body = new String(raw, StandardCharsets.UTF_8);
        System.out.println(">>> RAW BODY:\n" + body);

        // 2) Extrai o JSON dentro de data= ou assume JSON direto
        String json;
        if (body.startsWith("data=")) {
            // decodifica URL-encoding do valor após "data="
            json = URLDecoder.decode(body.substring(5), StandardCharsets.UTF_8);
        } else {
            json = body;
        }
        System.out.println(">>> JSON EXTRAÍDO:\n" + json);

        // 3) Desserializa para seu DTO
        WebhookPayloadDTO payload = objectMapper.readValue(json, WebhookPayloadDTO.class);

        // 4) Validação
        if (payload.getRetorno() == null
                || payload.getRetorno().getPedidos() == null
                || payload.getRetorno().getPedidos().isEmpty()) {
            throw new IllegalArgumentException("Payload inválido: sem pedidos");
        }

        // 5) Processa
        PedidoDTO pedido = payload.getRetorno()
                .getPedidos()
                .get(0)
                .getPedido();
        return orderService.enviarDados(pedido);
    }
}

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

    @PostMapping(path = "", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String processRaw(@RequestBody byte[] raw) throws Exception {
        // 1) transforma em String para inspecionar
        String body = new String(raw, StandardCharsets.UTF_8);
        System.out.println("RAW BODY >>> " + body);

        // 2) se for form-urlencoded data=..., faz decode
        String json;
        if (body.startsWith("data=")) {
            // url decode só o valor após "data="
            json = URLDecoder.decode(body.substring(5), StandardCharsets.UTF_8);
        } else {
            // assume que veio JSON puro
            json = body;
        }
        System.out.println("DECODED JSON >>> " + json);

        // 3) desserializa pro seu DTO
        WebhookPayloadDTO payload = objectMapper.readValue(json, WebhookPayloadDTO.class);

        // 4) processa como antes
        PedidoDTO pedido = payload.getRetorno().getPedidos().get(0).getPedido();
        return orderService.enviarDados(pedido);
    }
}

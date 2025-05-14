package br.com.contis.producer.controller;

import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import br.com.contis.producer.dto.WebhookRequestDTO;
import br.com.contis.producer.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping
    public String process(
            HttpServletRequest request,
            @RequestParam(value = "data", required = false) String dataParam,
            @RequestBody(required = false) WebhookPayloadDTO jsonPayload
    ) {
        WebhookPayloadDTO payload;

        try {
            if (dataParam != null) {
                payload = objectMapper.readValue(dataParam, WebhookPayloadDTO.class);
            }
            else if (jsonPayload != null) {
                payload = jsonPayload;
            }
            else {
                String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                payload = objectMapper.readValue(body, WebhookPayloadDTO.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Não foi possível desserializar o payload", e);
        }

        if (payload.getRetorno() == null
                || payload.getRetorno().getPedidos() == null
                || payload.getRetorno().getPedidos().isEmpty()) {
            throw new IllegalArgumentException("Payload inválido: sem pedidos");
        }

        PedidoDTO pedido = payload.getRetorno().getPedidos().get(0).getPedido();
        return orderService.enviarDados(pedido);
    }

}

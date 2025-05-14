package br.com.contis.producer.controller;

import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import br.com.contis.producer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping
    public String process(@RequestBody WebhookPayloadDTO order) {
        PedidoDTO pedido = order.getRetorno().getPedidos().get(0).getPedido();

        return orderService.enviarDados(pedido);

    }
}

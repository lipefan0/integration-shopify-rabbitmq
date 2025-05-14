package br.com.contis.consumer.component;

import br.com.contis.consumer.dto.PedidoDTO;
import br.com.contis.consumer.service.ShopifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class OrderRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderRequestConsumer.class);

    @Autowired
    private ShopifyService shopifyService;

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = { "bling-orders" })
    public void consume(PedidoDTO pedido) {
        try {
            logger.info("Pedido recebido: {} e tipo {}", pedido.getNumeroPedidoLoja(), pedido.getTipoIntegracao());

            // Verifica se é um pedido Shopify
            if ("Shopify".equalsIgnoreCase(pedido.getTipoIntegracao())) {

                // Verifica se tem código de rastreamento válido
                String codigoRastreamento = null;
                if (pedido.getCodigosRastreamento() != null) {
                    codigoRastreamento = pedido.getCodigosRastreamento().getCodigoRastreamento();
                }

                if (codigoRastreamento != null && !codigoRastreamento.trim().isEmpty()) {
                    logger.info("Processando pedido Shopify: {} com código de rastreamento: {}",
                            pedido.getNumeroPedidoLoja(), codigoRastreamento);
                    shopifyService.createFulfillmentShopify(pedido);
                } else {
                    logger.info("Ignorando pedido Shopify: {} pois não possui código de rastreamento válido",
                            pedido.getNumeroPedidoLoja());
                }
            } else {
                logger.info("Pedido não é do Shopify, ignorando: {} - Tipo: {}",
                        pedido.getNumero(), pedido.getTipoIntegracao());
            }
        } catch (Exception e) {
            logger.error("Erro ao processar pedido {}: {}", pedido.getNumeroPedidoLoja(), e.getMessage(), e);
        }
    }
}

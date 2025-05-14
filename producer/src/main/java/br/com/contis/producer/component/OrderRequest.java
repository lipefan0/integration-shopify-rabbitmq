package br.com.contis.producer.component;

import br.com.contis.producer.config.MessageConverterConfig;
import br.com.contis.producer.dto.PedidoDTO;
import br.com.contis.producer.dto.WebhookPayloadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.contis.producer.config.FilaRabbitMQConfig.NOME_EXCHANGE;
import static br.com.contis.producer.config.FilaRabbitMQConfig.ROUTING_KEY;

@Component
public class OrderRequest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private MessageConverterConfig messageConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(PedidoDTO payload) throws JsonProcessingException {

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = messageConverter.jsonMessageConverter().toMessage(payload, messageProperties);

        amqpTemplate.send(
                NOME_EXCHANGE,
                ROUTING_KEY,
                message
        );
    }

}

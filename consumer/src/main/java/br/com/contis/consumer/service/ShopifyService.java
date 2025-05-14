package br.com.contis.consumer.service;

import br.com.contis.consumer.component.OrderRequestConsumer;
import br.com.contis.consumer.config.WebClientConfig;
import br.com.contis.consumer.dto.PedidoDTO;
import br.com.contis.consumer.graphql.GraphQLRequest;
import br.com.contis.consumer.graphql.GraphQLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopifyService {

    private static final Logger logger = LoggerFactory.getLogger(ShopifyService.class);

    private final WebClient webClient;

    @Autowired
    public ShopifyService(WebClient shopifyWebClient) {
        this.webClient = shopifyWebClient;
    }


    public void createFulfillmentShopify(PedidoDTO pedido) {

        try {
            String numeroPedidoLoja = pedido.getNumeroPedidoLoja();
            String codigoRastreamento = pedido.getCodigosRastreamento().getCodigoRastreamento();
            String empresaDeEnvio = "Correios";

            logger.info("Iniciando processo de fulfillment para pedido Shopify: {}, código de rastreamento: {}",
                    numeroPedidoLoja, codigoRastreamento);

            // Etapa 1: Converter o número do pedido da loja para o formato de ID do GraphQL do Shopify (gid)
            String shopifyOrderGid = formatShopifyGid(numeroPedidoLoja);

            // Etapa 2: Obter o fulfillmentOrder ID
            String fulfillmentOrderId = getFulfillmentOrderId(shopifyOrderGid)
                    .block(); // Convertendo para síncrono para simplicidade

            if (fulfillmentOrderId == null) {
                throw new RuntimeException("Não foi possível obter o fulfillmentOrder ID para o pedido " + numeroPedidoLoja);
            }

            logger.info("FulfillmentOrder ID obtido: {} para pedido: {}", fulfillmentOrderId, numeroPedidoLoja);

            // Etapa 3: Criar o fulfillment com o código de rastreamento
            boolean success = createFulfillment(fulfillmentOrderId, codigoRastreamento, empresaDeEnvio)
                    .block(); // Convertendo para síncrono para simplicidade

            if (success) {
                logger.info("Fulfillment criado com sucesso para o pedido: {}", numeroPedidoLoja);
            } else {
                logger.error("Falha ao criar fulfillment para o pedido: {}", numeroPedidoLoja);
                throw new RuntimeException("Falha ao criar fulfillment para o pedido " + numeroPedidoLoja);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar fulfillment para pedido {}: {}",
                    pedido.getNumeroPedidoLoja(), e.getMessage(), e);
            throw new RuntimeException("Falha ao criar fulfillment no Shopify", e);
        }
    }

    private String formatShopifyGid(String orderId) {
        // O formato Shopify GID é normalmente: gid://shopify/Order/12345678
        return "gid://shopify/Order/" + orderId;
    }

    private Mono<String> getFulfillmentOrderId(String orderGid) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderGid", orderGid);

        GraphQLRequest request = new GraphQLRequest(getFulfillmentByOrderId(), variables);

        return webClient.post()
                .uri("")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<Map<String, Object>>>() {})
                .map(response -> {
                    validateGraphQLResponse(response, "Erro ao obter fulfillmentOrder");

                    // Navegando pela estrutura de resposta para extrair o fulfillmentOrder ID
                    try {
                        Map<String, Object> data = response.getData();
                        Map<String, Object> order = (Map<String, Object>) data.get("order");
                        Map<String, Object> fulfillmentOrders = (Map<String, Object>) order.get("fulfillmentOrders");
                        List<Map<String, Object>> edges = (List<Map<String, Object>>) fulfillmentOrders.get("edges");

                        if (edges == null || edges.isEmpty()) {
                            throw new RuntimeException("Não há fulfillmentOrders disponíveis para este pedido");
                        }

                        Map<String, Object> node = (Map<String, Object>) edges.get(0).get("node");
                        return (String) node.get("id");
                    } catch (Exception e) {
                        logger.error("Erro ao extrair fulfillmentOrder ID da resposta: {}", e.getMessage());
                        throw new RuntimeException("Erro ao processar resposta do Shopify", e);
                    }
                });
    }

    private Mono<Boolean> createFulfillment(String fulfillmentOrderId, String trackingNumber, String shippingCompany) {
        // Criando a estrutura correta para fulfillmentCreateV2 conforme API atual do Shopify
        Map<String, Object> trackingInfo = new HashMap<>();
        trackingInfo.put("number", trackingNumber);
        trackingInfo.put("company", shippingCompany);

        // Criamos um objeto para representar o lineItemsByFulfillmentOrder
        Map<String, Object> lineItemsByFulfillmentOrder = new HashMap<>();
        lineItemsByFulfillmentOrder.put("fulfillmentOrderId", fulfillmentOrderId);

        // Estrutura correta para o input FulfillmentV2Input
        Map<String, Object> fulfillment = new HashMap<>();
        fulfillment.put("trackingInfo", trackingInfo);
        fulfillment.put("notifyCustomer", true);
        fulfillment.put("lineItemsByFulfillmentOrder", List.of(lineItemsByFulfillmentOrder));

        Map<String, Object> variables = new HashMap<>();
        variables.put("fulfillment", fulfillment);

        GraphQLRequest request = new GraphQLRequest(createFulfillmentWithTracking(), variables);

        return webClient.post()
                .uri("")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<Map<String, Object>>>() {})
                .map(response -> {
                    try {
                        validateGraphQLResponse(response, "Erro ao criar fulfillment");

                        // Verificar se há userErrors na resposta
                        Map<String, Object> data = response.getData();
                        Map<String, Object> fulfillmentCreateV2 = (Map<String, Object>) data.get("fulfillmentCreateV2");
                        List<Map<String, Object>> userErrors = (List<Map<String, Object>>) fulfillmentCreateV2.get("userErrors");

                        if (userErrors != null && !userErrors.isEmpty()) {
                            StringBuilder errorMsg = new StringBuilder("Erros na criação do fulfillment: ");
                            for (Map<String, Object> error : userErrors) {
                                errorMsg.append(error.get("message")).append("; ");
                            }
                            logger.error(errorMsg.toString());
                            return false;
                        }

                        // Sucesso na criação do fulfillment
                        Map<String, Object> fulfillmentResult = (Map<String, Object>) fulfillmentCreateV2.get("fulfillment");
                        String fulfillmentId = (String) fulfillmentResult.get("id");
                        logger.info("Fulfillment criado com ID: {}", fulfillmentId);

                        return true;
                    } catch (Exception e) {
                        logger.error("Erro ao processar resposta de criação de fulfillment: {}", e.getMessage());
                        return false;
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Erro na comunicação com Shopify: {}", error.getMessage());
                    return Mono.just(false);
                });
    }

    private void validateGraphQLResponse(GraphQLResponse<?> response, String errorPrefix) {
        if (response.getErrors() != null && response.getErrors().length > 0) {
            StringBuilder errorMsg = new StringBuilder(errorPrefix).append(": ");
            for (GraphQLResponse.GraphQLError error : response.getErrors()) {
                errorMsg.append(error.getMessage()).append("; ");
            }
            logger.error(errorMsg.toString());
            throw new RuntimeException(errorMsg.toString());
        }

        if (response.getData() == null) {
            throw new RuntimeException(errorPrefix + ": Resposta sem dados");
        }
    }

    private String getFulfillmentByOrderId() {
        return """
                query($orderGid: ID!) {
                  order(id: $orderGid) {
                    fulfillmentOrders(first: 1) {
                      edges {
                        node {
                          id
                        }
                      }
                    }
                  }
                }
                """;
    }

    private String createFulfillmentWithTracking() {
        return """
        mutation fulfillmentCreateV2($fulfillment: FulfillmentV2Input!) {
          fulfillmentCreateV2(fulfillment: $fulfillment) {
            fulfillment {
              id
              trackingInfo {
                number
                company
              }
            }
            userErrors {
              field
              message
            }
          }
        }
    """;
    }
}

package br.com.contis.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PedidoDTO {

    private String numero;
    private String data;
    private String totalvenda;
    private String totalprodutos;
    private String situacao;
    private String loja;
    private String tipoIntegracao;
    private String numeroPedidoLoja;
    private String observacaointerna;

    private CodigoRastreamentoDTO codigosRastreamento;
}

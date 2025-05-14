package br.com.contis.producer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("codigosRastreamento")
    private CodigoRastreamentoDTO codigosRastreamento;


    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTotalvenda() {
        return totalvenda;
    }

    public void setTotalvenda(String totalvenda) {
        this.totalvenda = totalvenda;
    }

    public String getTotalprodutos() {
        return totalprodutos;
    }

    public void setTotalprodutos(String totalprodutos) {
        this.totalprodutos = totalprodutos;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getTipoIntegracao() {
        return tipoIntegracao;
    }

    public void setTipoIntegracao(String tipoIntegracao) {
        this.tipoIntegracao = tipoIntegracao;
    }

    public String getNumeroPedidoLoja() {
        return numeroPedidoLoja;
    }

    public void setNumeroPedidoLoja(String numeroPedidoLoja) {
        this.numeroPedidoLoja = numeroPedidoLoja;
    }

    public String getObservacaointerna() {
        return observacaointerna;
    }

    public void setObservacaointerna(String observacaointerna) {
        this.observacaointerna = observacaointerna;
    }

    public CodigoRastreamentoDTO getCodigosRastreamento() {
        return codigosRastreamento;
    }

    public void setCodigosRastreamento(CodigoRastreamentoDTO codigosRastreamento) {
        this.codigosRastreamento = codigosRastreamento;
    }
}

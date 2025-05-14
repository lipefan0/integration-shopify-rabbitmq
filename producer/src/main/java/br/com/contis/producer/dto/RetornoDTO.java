package br.com.contis.producer.dto;

import java.util.List;

public class RetornoDTO {

    List<PedidoWrapperDTO> pedidos;

    public List<PedidoWrapperDTO> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<PedidoWrapperDTO> pedidos) {
        this.pedidos = pedidos;
    }
}

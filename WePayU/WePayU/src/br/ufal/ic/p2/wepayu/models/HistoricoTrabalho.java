package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class HistoricoTrabalho {
    private final List<CartaoPonto> cartoesPonto = new ArrayList<>();
    private final List<Venda> vendas = new ArrayList<>();

    public List<CartaoPonto> getCartoesPonto() {
        return List.copyOf(cartoesPonto);
    }

    public List<Venda> getVendas() {
        return List.copyOf(vendas);
    }

    public void adicionarCartao(String data, double horas) {
        cartoesPonto.add(new CartaoPonto(data, horas));
    }

    public void adicionarVenda(String data, BigDecimal valor) {
        vendas.add(new Venda(data, valor));
    }

    public void adicionarVenda(String data, double valor) {
        adicionarVenda(data, BigDecimal.valueOf(valor));
    }

    void copiarPara(HistoricoTrabalho destino) {
        destino.cartoesPonto.addAll(cartoesPonto);
        destino.vendas.addAll(vendas);
    }
}

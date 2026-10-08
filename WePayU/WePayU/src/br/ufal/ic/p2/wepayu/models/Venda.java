package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.util.ValidadorData;

public class Venda {
    private final String data;
    private final double valor;

    public Venda(String data, double valor) {
        ValidadorData.parse(data);
        this.data = data;
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException();
        }
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }
}

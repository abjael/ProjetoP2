package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.util.ValidadorData;

public class CartaoPonto {
    private final String data;
    private final double horas;

    public CartaoPonto(String data, double horas) {
        ValidadorData.parse(data);
        this.data = data;
        if (!Double.isFinite(horas) || horas <= 0) {
            throw new IllegalArgumentException();
        }
        this.horas = horas;
    }

    public String getData() {
        return data;
    }

    public double getHoras() {
        return horas;
    }
}

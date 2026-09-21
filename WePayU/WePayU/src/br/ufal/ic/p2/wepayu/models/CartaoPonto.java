package br.ufal.ic.p2.wepayu.models;

public class CartaoPonto {
    private String data;
    private double horas;

    public CartaoPonto(String data, double horas) {
        this.data = data;
        this.horas = horas;
    }

    public String getData() {
        return data;
    }

    public double getHoras() {
        return horas;
    }
}
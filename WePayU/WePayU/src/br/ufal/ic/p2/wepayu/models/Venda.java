package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.util.ValidadorData;
import java.math.BigDecimal;
import java.util.Objects;

public class Venda {
    private final String data;
    private final BigDecimal valor;

    public Venda(String data, double valor) {
        this(data, BigDecimal.valueOf(valor));
    }

    public Venda(String data, BigDecimal valor) {
        ValidadorData.parse(data);
        this.data = data;
        Objects.requireNonNull(valor);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException();
        }
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public double getValor() { return valor.doubleValue(); }
    public BigDecimal getValorMonetario() { return valor; }
}

package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Horista extends Empregado {
    private List<CartaoPonto> cartoes;

    public Horista(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
        this.cartoes = new ArrayList<>();
    }

    public void lancarCartao(String data, double horas) {
        this.cartoes.add(new CartaoPonto(data, horas));
    }

    public double getHorasNormais(String dataInicial, String dataFinal) {
        LocalDate inicio = parseData(dataInicial);
        LocalDate fim = parseData(dataFinal);
        double total = 0.0;

        for (CartaoPonto cartao : cartoes) {
            LocalDate dataCartao = parseData(cartao.getData());
            if (!dataCartao.isBefore(inicio) && dataCartao.isBefore(fim)) {
                double h = cartao.getHoras();
                if (h <= 8.0) {
                    total += h;
                } else {
                    total += 8.0;
                }
            }
        }
        return total;
    }

    public double getHorasExtras(String dataInicial, String dataFinal) {
        LocalDate inicio = parseData(dataInicial);
        LocalDate fim = parseData(dataFinal);
        double total = 0.0;

        for (CartaoPonto cartao : cartoes) {
            LocalDate dataCartao = parseData(cartao.getData());
            if (!dataCartao.isBefore(inicio) && dataCartao.isBefore(fim)) {
                double h = cartao.getHoras();
                if (h > 8.0) {
                    total += (h - 8.0);
                }
            }
        }
        return total;
    }

    private LocalDate parseData(String data) {
        String[] partes = data.split("/");
        return LocalDate.of(Integer.parseInt(partes[2]), Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
    }
}
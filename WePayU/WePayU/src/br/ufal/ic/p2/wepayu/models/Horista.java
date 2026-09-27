package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.DayOfWeek;
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
                total += Math.min(h, 8.0);
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


    private double getHorasNormaisInclusivo(LocalDate inicio, LocalDate fim) {
        double total = 0.0;
        for (CartaoPonto cartao : cartoes) {
            LocalDate dataCartao = parseData(cartao.getData());
            if (!dataCartao.isBefore(inicio) && !dataCartao.isAfter(fim)) {
                double h = cartao.getHoras();
                total += Math.min(h, 8.0);
            }
        }
        return total;
    }

    private double getHorasExtrasInclusivo(LocalDate inicio, LocalDate fim) {
        double total = 0.0;
        for (CartaoPonto cartao : cartoes) {
            LocalDate dataCartao = parseData(cartao.getData());
            if (!dataCartao.isBefore(inicio) && !dataCartao.isAfter(fim)) {
                double h = cartao.getHoras();
                if (h > 8.0) {
                    total += (h - 8.0);
                }
            }
        }
        return total;
    }

    @Override
    public boolean ehDiaDePagamento(String data) {
        LocalDate d = parseData(data);
        return d.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    @Override
    public double calcularSalarioBruto(String dataStr) {
        LocalDate dataFim = parseData(dataStr);
        LocalDate dataInicio = dataFim.minusDays(6);

        double horasNormais = getHorasNormaisInclusivo(dataInicio, dataFim);
        double horasExtras = getHorasExtrasInclusivo(dataInicio, dataFim);

        double valorHora = getSalario();
        return (horasNormais * valorHora) + (horasExtras * valorHora * 1.5);
    }

    @Override
    public double calcularSalarioLiquido(String dataStr) {
        LocalDate dataFim = parseData(dataStr);
        LocalDate dataInicio = getUltimaDataPagamento().plusDays(1);
        long diasDecorridos = java.time.temporal.ChronoUnit.DAYS.between(getUltimaDataPagamento(), dataFim);

        double bruto = calcularSalarioBruto(dataStr);

        double descontoSindical = 0.0;
        if ("true".equals(getSindicalizado())) {
            descontoSindical = getTaxaSindical() * diasDecorridos;
        }

        double totalTaxasServico = 0.0;
        for (TaxaServico ts : getTaxasServico()) {
            LocalDate dataTs = parseData(ts.getData());
            if (!dataTs.isBefore(dataInicio) && !dataTs.isAfter(dataFim)) {
                totalTaxasServico += ts.getValor();
            }
        }

        return Math.max(0.0, bruto - descontoSindical - totalTaxasServico);
    }
}
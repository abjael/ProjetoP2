package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Assalariado extends Empregado {

    public Assalariado(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
    }

    @Override
    public boolean ehDiaDePagamento(String data) {
        LocalDate d = parseData(data);
        LocalDate ultimoDiaMes = d.withDayOfMonth(d.lengthOfMonth());
        return d.equals(ultimoDiaMes);
    }

    @Override
    public double calcularSalarioBruto(String dataStr) {
        return getSalario();
    }

    @Override
    public double calcularSalarioLiquido(String dataStr) {
        LocalDate dataFim = parseData(dataStr);
        LocalDate dataInicio = getUltimaDataPagamento().plusDays(1);
        long diasDecorridos = ChronoUnit.DAYS.between(getUltimaDataPagamento(), dataFim);

        double bruto = getSalario();

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
    @Override
    public Empregado clonar() {
        Assalariado copia = new Assalariado(getId(), getNome(), getEndereco(), getTipo(), getSalario(), getSindicalizado());
        copiarCamposComunsPara(copia);
        return copia;
    }
}
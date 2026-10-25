package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class CalculadoraDescontos {
    private CalculadoraDescontos() {
    }

    public static BigDecimal calcularSalarioLiquido(
            Empregado empregado, String data, BigDecimal salarioBruto) {
        LocalDate dataFim = empregado.parseData(data);
        LocalDate dataInicio = empregado.getUltimaDataPagamento().plusDays(1);
        long diasDecorridos = ChronoUnit.DAYS.between(
                empregado.getUltimaDataPagamento(), dataFim);
        BigDecimal descontoSindical = empregado.isSindicalizado()
                ? empregado.getTaxaSindicalMonetario()
                        .multiply(BigDecimal.valueOf(diasDecorridos))
                : BigDecimal.ZERO;
        BigDecimal totalTaxasServico = BigDecimal.ZERO;
        for (TaxaServico taxa : empregado.getTaxasServico()) {
            LocalDate dataTaxa = empregado.parseData(taxa.getData());
            if (!dataTaxa.isBefore(dataInicio) && !dataTaxa.isAfter(dataFim)) {
                totalTaxasServico = totalTaxasServico.add(
                        taxa.getValorMonetario());
            }
        }
        BigDecimal descontos = empregado.getDividaDescontosMonetario()
                .add(descontoSindical)
                .add(totalTaxasServico);
        BigDecimal liquido = salarioBruto.subtract(descontos)
                .max(BigDecimal.ZERO);
        empregado.setDividaDescontos(
                descontos.subtract(salarioBruto).max(BigDecimal.ZERO));
        return liquido;
    }
}

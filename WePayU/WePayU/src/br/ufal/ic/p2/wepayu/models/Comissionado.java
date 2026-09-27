package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Comissionado extends Empregado {
    private double comissao;
    private List<Venda> vendas;

    public Comissionado(String id, String nome, String endereco, String tipo, double salario, double comissao, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
        this.comissao = comissao;
        this.vendas = new ArrayList<>();
    }

    @Override
    public double getComissao() { return comissao; }

    public void adicionarVenda(String data, double valor) {
        this.vendas.add(new Venda(data, valor));
    }

    public List<Venda> getVendas() { return vendas; }
    public void setComissao(double comissao) { this.comissao = comissao; }

    @Override
    public boolean ehDiaDePagamento(String data) {
        LocalDate d = parseData(data);
        if (d.getDayOfWeek() != DayOfWeek.FRIDAY) {
            return false;
        }
        LocalDate referencia = LocalDate.of(2005, 1, 14);
        long semanas = ChronoUnit.WEEKS.between(referencia, d);
        return semanas >= 0 && semanas % 2 == 0;
    }

    @Override
    public double calcularSalarioBruto(String dataStr) {
        LocalDate dataFim = parseData(dataStr);
        LocalDate dataInicio = getUltimaDataPagamento().plusDays(1);

        double salarioQuinzenalBruto = getSalario() * 24.0 / 52.0;
        double salarioQuinzenal = Math.floor(salarioQuinzenalBruto * 100.0) / 100.0;

        double totalVendasPeriodo = 0.0;
        for (Venda v : vendas) {
            LocalDate dataV = parseData(v.getData());
            if (!dataV.isBefore(dataInicio) && !dataV.isAfter(dataFim)) {
                totalVendasPeriodo += v.getValor();
            }
        }

        double comissaoRecebida = Math.floor((totalVendasPeriodo * getComissao()) * 100.0) / 100.0;
        double bruto = salarioQuinzenal + comissaoRecebida;
        return Math.round(bruto * 100.0) / 100.0;
    }

    @Override
    public double calcularSalarioLiquido(String dataStr) {
        LocalDate dataFim = parseData(dataStr);
        LocalDate dataInicio = getUltimaDataPagamento().plusDays(1);
        long diasDecorridos = ChronoUnit.DAYS.between(getUltimaDataPagamento(), dataFim);

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
    @Override
    public Empregado clonar() {
        Comissionado copia = new Comissionado(getId(), getNome(), getEndereco(), getTipo(), getSalario(), getComissao(), getSindicalizado());
        copiarCamposComunsPara(copia);
        for (Venda v : this.getVendas()) {
            copia.adicionarVenda(v.getData(), v.getValor());
        }
        return copia;
    }
}
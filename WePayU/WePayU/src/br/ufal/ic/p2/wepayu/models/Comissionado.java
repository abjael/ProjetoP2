package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.DescricaoDeAgendaInvalidaException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Comissionado extends Empregado {
    private double comissao;

    public Comissionado(String id, String nome, String endereco, double salario, double comissao, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        validarValorNaoNegativo(comissao);
        this.comissao = comissao;
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.COMISSIONADO));
    }

    @Override
    public void validarComissao() {
    }

    @Override
    public double getComissao() { return comissao; }

    @Override
    public void adicionarVenda(String data, double valor) {
        registrarVenda(data, valor);
    }

    @Override
    public void setComissao(double comissao) {
        validarValorNaoNegativo(comissao);
        this.comissao = comissao;
    }

    @Override
    public boolean ehDiaDePagamento(String data) {
        LocalDate d = parseData(data);
        try {
            return AgendaPagamento.parse(getAgendaPagamento()).ehDiaDePagamento(d);
        } catch (DescricaoDeAgendaInvalidaException e) {
            if (d.getDayOfWeek() != DayOfWeek.FRIDAY) {
                return false;
            }
            LocalDate referencia = LocalDate.of(2005, 1, 14);
            long semanas = ChronoUnit.WEEKS.between(referencia, d);
            return semanas >= 0 && semanas % 2 == 0;
        }
    }

    @Override
    public double calcularSalarioBruto(String dataStr)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioBruto(
                dataStr, getUltimaDataPagamento().plusDays(1));
    }

    @Override
    public double calcularSalarioBruto(
            String dataStr, LocalDate dataInicio)
            throws DescricaoDeAgendaInvalidaException {
        LocalDate dataFim = parseData(dataStr);

        double salarioPeriodo = AgendaPagamento.parse(getAgendaPagamento())
                .salarioDoPeriodo(getSalario());

        double totalVendasPeriodo = 0.0;
        for (Venda venda : getVendas()) {
            LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(dataInicio)
                    && !dataVenda.isAfter(dataFim)) {
                totalVendasPeriodo += venda.getValor();
            }
        }

        double comissaoRecebida = Math.floor(
                totalVendasPeriodo * getComissao() * 100.0) / 100.0;
        return salarioPeriodo + comissaoRecebida;
    }

    @Override
    public Empregado clonar() {
        Comissionado copia = new Comissionado(
                getId(), getNome(), getEndereco(), getSalario(), getComissao(), false);
        copiarCamposComunsPara(copia);
        return copia;
    }
    @Override
    public void validarLancamentoVenda() {
    }
    @Override
    public void validarConsultaVendas() {
    }

    @Override
    public double calcularVendasRealizadas(
            LocalDate inicio, LocalDate fim) {
        double totalVendas = 0.0;

        for (Venda venda : getVendas()) {
            LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(inicio) && dataVenda.isBefore(fim)) {
                totalVendas += venda.getValor();
            }
        }

        return totalVendas;
    }
    @Override
    public void aceitar(VisitanteEmpregado visitante) {
        visitante.visitar(this);
    }

    @Override
    public void aceitar(VisitanteDadosEmpregado visitante) {
        visitante.visitar(this);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.COMISSIONADO;
    }
}

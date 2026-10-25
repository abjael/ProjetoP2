package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Comissionado extends Empregado {
    private BigDecimal comissao;

    public Comissionado(String id, String nome, String endereco, double salario, double comissao, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        validarValorNaoNegativo(comissao);
        this.comissao = BigDecimal.valueOf(comissao);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.COMISSIONADO));
    }

    public Comissionado(
            String id, String nome, String endereco, BigDecimal salario,
            BigDecimal comissao, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        validarValorNaoNegativo(comissao);
        this.comissao = comissao;
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.COMISSIONADO));
    }

    public double getComissao() { return comissao.doubleValue(); }
    public BigDecimal getComissaoMonetario() { return comissao; }

    public void adicionarVenda(String data, double valor) {
        getHistoricoTrabalho().adicionarVenda(data, valor);
    }

    public void setComissao(double comissao) {
        validarValorNaoNegativo(comissao);
        this.comissao = BigDecimal.valueOf(comissao);
    }

    public void setComissao(BigDecimal comissao) {
        validarValorNaoNegativo(comissao);
        this.comissao = comissao;
    }

    public List<Venda> getVendas() {
        return getHistoricoTrabalho().getVendas();
    }

    public void adicionarVenda(String data, BigDecimal valor) {
        getHistoricoTrabalho().adicionarVenda(data, valor);
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
        return calcularSalarioBrutoMonetario(
                dataStr, getUltimaDataPagamento().plusDays(1)).doubleValue();
    }

    @Override
    public double calcularSalarioBruto(
            String dataStr, LocalDate dataInicio)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioBrutoMonetario(dataStr, dataInicio).doubleValue();
    }

    @Override
    public BigDecimal calcularSalarioBrutoMonetario(String dataStr)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioBrutoMonetario(
                dataStr, getUltimaDataPagamento().plusDays(1));
    }

    @Override
    public BigDecimal calcularSalarioBrutoMonetario(
            String dataStr, LocalDate dataInicio)
            throws DescricaoDeAgendaInvalidaException {
        LocalDate dataFim = parseData(dataStr);

        BigDecimal salarioPeriodo = AgendaPagamento.parse(getAgendaPagamento())
                .salarioDoPeriodo(getSalarioMonetario());

        BigDecimal totalVendasPeriodo = BigDecimal.ZERO;
        for (Venda venda : getVendas()) {
            LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(dataInicio)
                    && !dataVenda.isAfter(dataFim)) {
                totalVendasPeriodo = totalVendasPeriodo.add(
                        venda.getValorMonetario());
            }
        }

        BigDecimal comissaoRecebida = totalVendasPeriodo
                .multiply(getComissaoMonetario())
                .setScale(2, RoundingMode.DOWN);
        return salarioPeriodo.add(comissaoRecebida);
    }

    @Override
    public Empregado clonar() {
        Comissionado copia = new Comissionado(
                getId(), getNome(), getEndereco(), getSalarioMonetario(),
                getComissaoMonetario(), false);
        copiarCamposComunsPara(copia);
        return copia;
    }

    public double calcularVendasRealizadas(
            LocalDate inicio, LocalDate fim) {
        return calcularVendasRealizadasMonetario(inicio, fim).doubleValue();
    }

    public BigDecimal calcularVendasRealizadasMonetario(
            LocalDate inicio, LocalDate fim) {
        BigDecimal totalVendas = BigDecimal.ZERO;
        for (Venda venda : getVendas()) {
            LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(inicio) && dataVenda.isBefore(fim)) {
                totalVendas = totalVendas.add(venda.getValorMonetario());
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

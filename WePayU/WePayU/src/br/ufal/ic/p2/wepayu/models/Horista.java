package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.exception.DescricaoDeAgendaInvalidaException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Horista extends Empregado {
    public Horista(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.HORISTA));
    }

    @Override
    public void lancarCartao(String data, double horas) {
        registrarCartao(data, horas);
    }
    @Override
    public double getHorasNormais(String dataInicial, String dataFinal) {
        LocalDate inicio = parseData(dataInicial);
        LocalDate fim = parseData(dataFinal);
        double total = 0.0;

        for (CartaoPonto cartao : getCartoes()) {
            LocalDate dataCartao = parseData(cartao.getData());
            if (!dataCartao.isBefore(inicio) && dataCartao.isBefore(fim)) {
                double h = cartao.getHoras();
                total += Math.min(h, 8.0);
            }
        }
        return total;
    }

    @Override
    public double getHorasExtras(String dataInicial, String dataFinal) {
        LocalDate inicio = parseData(dataInicial);
        LocalDate fim = parseData(dataFinal);
        double total = 0.0;

        for (CartaoPonto cartao : getCartoes()) {
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
        for (CartaoPonto cartao : getCartoes()) {
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
        for (CartaoPonto cartao : getCartoes()) {
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
        if (getAgendaPagamento() == null) {
            return d.getDayOfWeek() == DayOfWeek.FRIDAY;
        }
        try {
            return AgendaPagamento.parse(getAgendaPagamento()).ehDiaDePagamento(d);
        } catch (DescricaoDeAgendaInvalidaException e) {
            return d.getDayOfWeek() == DayOfWeek.FRIDAY;
        }
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
    public Empregado clonar() {
        Horista copia = new Horista(getId(), getNome(), getEndereco(), getSalario(), false);
        copiarCamposComunsPara(copia);
        return copia;
    }
    @Override
    public void validarLancamentoCartao() {
    }
    @Override
    public void validarConsultaHoras() {
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
        return TipoEmpregado.HORISTA;
    }

    @Override
    public void validarComissao() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    @Override
    public double getComissao() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    @Override
    public void setComissao(double comissao) throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }
}

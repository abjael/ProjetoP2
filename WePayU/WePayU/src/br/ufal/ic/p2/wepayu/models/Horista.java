package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.math.BigDecimal;

public class Horista extends Empregado {
    public Horista(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.HORISTA));
    }

    public Horista(
            String id, String nome, String endereco, BigDecimal salario,
            boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.HORISTA));
    }

    public void lancarCartao(String data, double horas) {
        getHistoricoTrabalho().adicionarCartao(data, horas);
    }

    public List<CartaoPonto> getCartoes() {
        return getHistoricoTrabalho().getCartoesPonto();
    }

    public double getHorasNormais(String dataInicial, String dataFinal) {
        return somarHoras(parseData(dataInicial), parseData(dataFinal), false, false);
    }

    public double getHorasExtras(String dataInicial, String dataFinal) {
        return somarHoras(parseData(dataInicial), parseData(dataFinal), true, false);
    }

    private double somarHoras(
            LocalDate inicio, LocalDate fim, boolean extras, boolean incluirFim) {
        double total = 0.0;
        for (CartaoPonto cartao : getCartoes()) {
            LocalDate dataCartao = parseData(cartao.getData());
            boolean dentroDoPeriodo = !dataCartao.isBefore(inicio)
                    && (incluirFim
                    ? !dataCartao.isAfter(fim)
                    : dataCartao.isBefore(fim));
            if (dentroDoPeriodo) {
                double horas = cartao.getHoras();
                total += extras ? Math.max(0.0, horas - 8.0)
                        : Math.min(horas, 8.0);
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
        return calcularSalarioBrutoMonetario(
                dataStr, getUltimaDataPagamento().plusDays(1)).doubleValue();
    }

    @Override
    public double calcularSalarioBruto(
            String dataStr, LocalDate dataInicio) {
        return calcularSalarioBrutoMonetario(dataStr, dataInicio).doubleValue();
    }

    @Override
    public BigDecimal calcularSalarioBrutoMonetario(String dataStr) {
        return calcularSalarioBrutoMonetario(
                dataStr, getUltimaDataPagamento().plusDays(1));
    }

    @Override
    public BigDecimal calcularSalarioBrutoMonetario(
            String dataStr, LocalDate dataInicio) {
        LocalDate dataFim = parseData(dataStr);

        double horasNormais = somarHoras(dataInicio, dataFim, false, true);
        double horasExtras = somarHoras(dataInicio, dataFim, true, true);

        BigDecimal valorHora = getSalarioMonetario();
        BigDecimal pagamentoNormal = valorHora.multiply(
                BigDecimal.valueOf(horasNormais));
        BigDecimal pagamentoExtra = valorHora
                .multiply(BigDecimal.valueOf(horasExtras))
                .multiply(new BigDecimal("1.5"));
        return pagamentoNormal.add(pagamentoExtra);
    }
    @Override
    public Empregado clonar() {
        Horista copia = new Horista(
                getId(), getNome(), getEndereco(), getSalarioMonetario(), false);
        copiarCamposComunsPara(copia);
        return copia;
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

}

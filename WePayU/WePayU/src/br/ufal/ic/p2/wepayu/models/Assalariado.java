package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Assalariado extends Empregado {

    public Assalariado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.ASSALARIADO));
    }

    public Assalariado(
            String id, String nome, String endereco, BigDecimal salario,
            boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
        setAgendaPagamento(AgendaPagamento.defaultAgenda(TipoEmpregado.ASSALARIADO));
    }

    @Override
    public boolean ehDiaDePagamento(String data) {
        LocalDate d = parseData(data);
        try {
            return AgendaPagamento.parse(getAgendaPagamento()).ehDiaDePagamento(d);
        } catch (DescricaoDeAgendaInvalidaException e) {
            LocalDate ultimoDiaMes = d.withDayOfMonth(d.lengthOfMonth());
            return d.equals(ultimoDiaMes);
        }
    }

    @Override
    public double calcularSalarioBruto(String dataStr)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioBrutoMonetario(dataStr).doubleValue();
    }

    @Override
    public BigDecimal calcularSalarioBrutoMonetario(String dataStr)
            throws DescricaoDeAgendaInvalidaException {
        return AgendaPagamento.parse(getAgendaPagamento())
                .salarioDoPeriodo(getSalarioMonetario());
    }

    @Override
    public Empregado clonar() {
        Assalariado copia = new Assalariado(
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
        return TipoEmpregado.ASSALARIADO;
    }

}

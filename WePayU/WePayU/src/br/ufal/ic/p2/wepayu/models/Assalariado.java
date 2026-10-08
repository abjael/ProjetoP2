package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.DescricaoDeAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.exception.EmpregadoNaoEhComissionadoException;
import java.time.LocalDate;

public class Assalariado extends Empregado {

    public Assalariado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
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
    public double calcularSalarioBruto(String dataStr) {
        return getSalario();
    }

    @Override
    public Empregado clonar() {
        Assalariado copia = new Assalariado(getId(), getNome(), getEndereco(), getSalario(), false);
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

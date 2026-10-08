package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionado;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Assalariado extends Empregado {

    public Assalariado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        super(id, nome, endereco, salario, sindicalizado);
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
        if (isSindicalizado()) {
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
        Assalariado copia = new Assalariado(getId(), getNome(), getEndereco(), getSalario(), isSindicalizado());
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
    public void validarComissao() throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }

    @Override
    public double getComissao() throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }

    @Override
    public void setComissao(double comissao) throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }
}

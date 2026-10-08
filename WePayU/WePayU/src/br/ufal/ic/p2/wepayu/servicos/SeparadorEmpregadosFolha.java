package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Horista;
import br.ufal.ic.p2.wepayu.models.VisitanteEmpregado;

import java.util.ArrayList;
import java.util.List;

public class SeparadorEmpregadosFolha implements VisitanteEmpregado {
    private final List<Horista> horistas = new ArrayList<>();
    private final List<Assalariado> assalariados = new ArrayList<>();
    private final List<Comissionado> comissionados = new ArrayList<>();

    @Override
    public void visitar(Horista horista) {
        horistas.add(horista);
    }

    @Override
    public void visitar(Assalariado assalariado) {
        assalariados.add(assalariado);
    }

    @Override
    public void visitar(Comissionado comissionado) {
        comissionados.add(comissionado);
    }

    public List<Horista> getHoristas() {
        return horistas;
    }

    public List<Assalariado> getAssalariados() {
        return assalariados;
    }

    public List<Comissionado> getComissionados() {
        return comissionados;
    }
}
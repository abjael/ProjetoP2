package br.ufal.ic.p2.wepayu.models;

public interface VisitanteDadosEmpregado {
    void visitar(Horista horista);
    void visitar(Assalariado assalariado);
    void visitar(Comissionado comissionado);
}

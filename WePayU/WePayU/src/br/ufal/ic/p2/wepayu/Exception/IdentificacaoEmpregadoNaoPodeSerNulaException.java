package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoEmpregadoNaoPodeSerNulaException extends Exception {
    public IdentificacaoEmpregadoNaoPodeSerNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}

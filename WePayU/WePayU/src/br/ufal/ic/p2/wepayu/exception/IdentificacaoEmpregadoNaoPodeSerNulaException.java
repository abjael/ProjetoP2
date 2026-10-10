package br.ufal.ic.p2.wepayu.exception;

public class IdentificacaoEmpregadoNaoPodeSerNulaException extends ErroWePayUException {
    public IdentificacaoEmpregadoNaoPodeSerNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}

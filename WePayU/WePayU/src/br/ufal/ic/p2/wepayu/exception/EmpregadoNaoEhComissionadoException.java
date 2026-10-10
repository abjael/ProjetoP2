package br.ufal.ic.p2.wepayu.exception;

public class EmpregadoNaoEhComissionadoException extends ErroWePayUException {
    public EmpregadoNaoEhComissionadoException() {
        super("Empregado nao eh comissionado.");
    }
}

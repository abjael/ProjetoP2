package br.ufal.ic.p2.wepayu.exception;

public class EmpregadoNaoEhHoristaException extends ErroWePayUException {
    public EmpregadoNaoEhHoristaException() {
        super("Empregado nao eh horista.");
    }
}

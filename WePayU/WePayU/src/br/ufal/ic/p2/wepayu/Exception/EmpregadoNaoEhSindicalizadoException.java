package br.ufal.ic.p2.wepayu.exception;

public class EmpregadoNaoEhSindicalizadoException extends ErroWePayUException {
    public EmpregadoNaoEhSindicalizadoException() {
        super("Empregado nao eh sindicalizado.");
    }
}

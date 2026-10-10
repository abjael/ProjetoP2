package br.ufal.ic.p2.wepayu.exception;

public class EmpregadoNaoRecebeEmBancoException extends ErroWePayUException {
    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ComissaoDeveSerNumericaException extends ErroWePayUException {
    public ComissaoDeveSerNumericaException() {
        super("Comissao deve ser numerica.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ValorDeveSerNumericoException extends ErroWePayUException {
    public ValorDeveSerNumericoException() {
        super("Valor deve ser numerico.");
    }
}

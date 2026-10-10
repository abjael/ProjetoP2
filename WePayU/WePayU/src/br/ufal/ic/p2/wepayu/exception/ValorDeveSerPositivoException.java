package br.ufal.ic.p2.wepayu.exception;

public class ValorDeveSerPositivoException extends ErroWePayUException {
    public ValorDeveSerPositivoException() {
        super("Valor deve ser positivo.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ValorDeveSerTrueOuFalseException extends ErroWePayUException {
    public ValorDeveSerTrueOuFalseException() {
        super("Valor deve ser true ou false.");
    }
}

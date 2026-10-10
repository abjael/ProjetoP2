package br.ufal.ic.p2.wepayu.exception;

public class ValorNaoPodeSerNuloException extends ErroWePayUException {
    public ValorNaoPodeSerNuloException() {
        super("Valor nao pode ser nulo.");
    }
}

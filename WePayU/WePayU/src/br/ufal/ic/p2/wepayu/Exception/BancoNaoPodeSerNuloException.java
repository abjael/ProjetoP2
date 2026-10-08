package br.ufal.ic.p2.wepayu.exception;

public class BancoNaoPodeSerNuloException extends ErroWePayUException {
    public BancoNaoPodeSerNuloException() {
        super("Banco nao pode ser nulo.");
    }
}

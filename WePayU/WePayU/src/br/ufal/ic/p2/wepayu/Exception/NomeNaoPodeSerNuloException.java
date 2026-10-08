package br.ufal.ic.p2.wepayu.exception;

public class NomeNaoPodeSerNuloException extends ErroWePayUException {
    public NomeNaoPodeSerNuloException() {
        super("Nome nao pode ser nulo.");
    }
}

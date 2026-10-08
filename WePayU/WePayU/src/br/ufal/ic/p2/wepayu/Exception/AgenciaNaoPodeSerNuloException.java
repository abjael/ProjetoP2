package br.ufal.ic.p2.wepayu.exception;

public class AgenciaNaoPodeSerNuloException extends ErroWePayUException {
    public AgenciaNaoPodeSerNuloException () {
        super("Agencia nao pode ser nulo.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ContaCorrenteNaoPodeSerNuloException extends ErroWePayUException {
    public ContaCorrenteNaoPodeSerNuloException() {
        super("Conta corrente nao pode ser nulo.");
    }
}

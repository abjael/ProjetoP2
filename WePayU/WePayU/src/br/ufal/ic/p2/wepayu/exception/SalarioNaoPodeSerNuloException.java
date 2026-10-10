package br.ufal.ic.p2.wepayu.exception;

public class SalarioNaoPodeSerNuloException extends ErroWePayUException {
    public SalarioNaoPodeSerNuloException() {
        super("Salario nao pode ser nulo.");
    }
}

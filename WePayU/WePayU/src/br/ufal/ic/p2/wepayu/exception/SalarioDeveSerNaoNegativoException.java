package br.ufal.ic.p2.wepayu.exception;

public class SalarioDeveSerNaoNegativoException extends ErroWePayUException {
    public SalarioDeveSerNaoNegativoException() {
        super("Salario deve ser nao-negativo.");
    }
}

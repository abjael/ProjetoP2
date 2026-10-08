package br.ufal.ic.p2.wepayu.exception;

public class ComissaoNaoPodeSerNulaException extends ErroWePayUException {
    public ComissaoNaoPodeSerNulaException() {
        super("Comissao nao pode ser nula.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ComissaoDeveSerNaoNegativaException extends ErroWePayUException {
    public ComissaoDeveSerNaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }
}

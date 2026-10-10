package br.ufal.ic.p2.wepayu.exception;

public class IdentificacaoDoMembroNaoPodeSerNulaException extends ErroWePayUException {
    public IdentificacaoDoMembroNaoPodeSerNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}

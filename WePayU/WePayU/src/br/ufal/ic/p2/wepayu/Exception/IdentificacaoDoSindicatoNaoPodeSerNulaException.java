package br.ufal.ic.p2.wepayu.exception;

public class IdentificacaoDoSindicatoNaoPodeSerNulaException extends ErroWePayUException {
    public IdentificacaoDoSindicatoNaoPodeSerNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}

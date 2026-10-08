package br.ufal.ic.p2.wepayu.exception;

public class TaxaSindicalDeveSerNaoNegativaException extends ErroWePayUException {
    public TaxaSindicalDeveSerNaoNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}

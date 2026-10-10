package br.ufal.ic.p2.wepayu.exception;

public class TaxaSindicalNaoPodeSerNulaException extends ErroWePayUException {
    public TaxaSindicalNaoPodeSerNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }
}

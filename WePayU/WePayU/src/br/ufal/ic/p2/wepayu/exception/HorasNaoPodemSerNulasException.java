package br.ufal.ic.p2.wepayu.exception;

public class HorasNaoPodemSerNulasException extends ErroWePayUException {
    public HorasNaoPodemSerNulasException() {
        super("Horas nao podem ser nulas.");
    }
}

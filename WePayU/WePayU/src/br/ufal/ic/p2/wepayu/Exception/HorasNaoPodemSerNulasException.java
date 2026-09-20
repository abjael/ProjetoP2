package br.ufal.ic.p2.wepayu.Exception;

public class HorasNaoPodemSerNulasException extends Exception {
    public HorasNaoPodemSerNulasException() {
        super("Horas nao podem ser nulas.");
    }
}

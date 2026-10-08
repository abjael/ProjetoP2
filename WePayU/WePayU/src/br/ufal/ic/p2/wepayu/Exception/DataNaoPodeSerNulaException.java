package br.ufal.ic.p2.wepayu.exception;

public class DataNaoPodeSerNulaException extends ErroWePayUException {
    public DataNaoPodeSerNulaException() {
        super("Data nao pode ser nula.");
    }
}

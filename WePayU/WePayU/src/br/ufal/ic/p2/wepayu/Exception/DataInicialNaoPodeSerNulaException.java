package br.ufal.ic.p2.wepayu.exception;

public class DataInicialNaoPodeSerNulaException extends ErroWePayUException {
    public DataInicialNaoPodeSerNulaException() {
        super("Data inicial nao pode ser nula.");
    }
}

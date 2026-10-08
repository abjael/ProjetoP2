package br.ufal.ic.p2.wepayu.exception;

public class DataFinalNaoPodeSerNulaException extends ErroWePayUException {
    public DataFinalNaoPodeSerNulaException() {
        super("Data final nao pode ser nula.");
    }
}

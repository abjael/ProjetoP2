package br.ufal.ic.p2.wepayu.exception;

public class DataInicialNaoPodeSerPosteriorException extends ErroWePayUException {
    public DataInicialNaoPodeSerPosteriorException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}

package br.ufal.ic.p2.wepayu.Exception;

public class DataInicialNaoPodeSerPosteriorException extends Exception {
    public DataInicialNaoPodeSerPosteriorException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}

package br.ufal.ic.p2.wepayu.Exception;

public class DataInicialNaoPodeSerPosteriorException extends Exception {
    public DataInicialNaoPodeSerPosteriorException() {
        super("Data inical nao pode ser posterior a final.");
    }
}

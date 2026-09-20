package br.ufal.ic.p2.wepayu.Exception;

public class DataInicialNaoPodeSerNulaException extends Exception {
    public DataInicialNaoPodeSerNulaException() {
        super("Data inicial nao pode ser nula.");
    }
}

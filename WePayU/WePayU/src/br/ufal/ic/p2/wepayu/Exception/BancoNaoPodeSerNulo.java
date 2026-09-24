package br.ufal.ic.p2.wepayu.Exception;

public class BancoNaoPodeSerNulo extends Exception {
    public BancoNaoPodeSerNulo() {
        super("Banco nao pode ser nulo.");
    }
}

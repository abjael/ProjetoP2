package br.ufal.ic.p2.wepayu.Exception;

public class AgenciaNaoPodeSerNulo extends Exception {
    public AgenciaNaoPodeSerNulo () {
        super("Agencia nao pode ser nulo.");
    }
}

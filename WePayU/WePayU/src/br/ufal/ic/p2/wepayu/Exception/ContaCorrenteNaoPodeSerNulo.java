package br.ufal.ic.p2.wepayu.Exception;

public class ContaCorrenteNaoPodeSerNulo extends Exception {
    public ContaCorrenteNaoPodeSerNulo() {
        super("Conta corrente nao pode ser nulo.");
    }
}

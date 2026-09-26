package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSIndicalNaoPodeSerNula extends Exception {
    public TaxaSIndicalNaoPodeSerNula() {
        super("Taxa sindical nao pode ser nula.");
    }
}

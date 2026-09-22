package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoDoMembroNaoPodeSerNula extends Exception {
    public IdentificacaoDoMembroNaoPodeSerNula() {
        super("Identificacao do membro nao pode ser nula.");
    }
}

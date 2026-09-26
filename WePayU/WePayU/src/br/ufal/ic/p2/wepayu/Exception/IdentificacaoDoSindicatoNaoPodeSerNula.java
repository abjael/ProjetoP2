package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoDoSindicatoNaoPodeSerNula extends Exception {
    public IdentificacaoDoSindicatoNaoPodeSerNula() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}

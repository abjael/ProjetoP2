package br.ufal.ic.p2.wepayu.Exception;

public class ArquivoDeSaidaNaoPodeSerNulo extends Exception {
    public ArquivoDeSaidaNaoPodeSerNulo() {
        super("Arquivo de saida nao pode ser nulo.");
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ArquivoDeSaidaNaoPodeSerNuloException extends ErroWePayUException {
    public ArquivoDeSaidaNaoPodeSerNuloException() {
        super("Arquivo de saida nao pode ser nulo.");
    }
}

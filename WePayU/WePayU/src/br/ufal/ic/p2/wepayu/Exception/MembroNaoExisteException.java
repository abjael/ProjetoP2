package br.ufal.ic.p2.wepayu.exception;

public class MembroNaoExisteException extends ErroWePayUException {
    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}

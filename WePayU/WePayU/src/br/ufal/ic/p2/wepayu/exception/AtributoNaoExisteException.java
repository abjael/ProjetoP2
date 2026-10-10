package br.ufal.ic.p2.wepayu.exception;

public class AtributoNaoExisteException extends ErroWePayUException {
    public AtributoNaoExisteException() {
        super("Atributo nao existe.");
    }
}

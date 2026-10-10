package br.ufal.ic.p2.wepayu.exception;

public abstract class ErroWePayUException extends Exception {
    protected ErroWePayUException(String mensagem) {
        super(mensagem);
    }

    protected ErroWePayUException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

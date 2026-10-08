package br.ufal.ic.p2.wepayu.exception;

public class SistemaEncerradoException extends ErroWePayUException {
    public SistemaEncerradoException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}

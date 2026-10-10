package br.ufal.ic.p2.wepayu.exception;

public class MetodoDePagamentoInvalidoException extends ErroWePayUException {
    public MetodoDePagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}

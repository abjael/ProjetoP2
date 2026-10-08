package br.ufal.ic.p2.wepayu.exception;

public class AgendaDePagamentosJaExisteException extends ErroWePayUException {
    public AgendaDePagamentosJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}

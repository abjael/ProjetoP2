package br.ufal.ic.p2.wepayu.exception;

public class AgendaDePagamentoNaoDisponivelException extends ErroWePayUException {
    public AgendaDePagamentoNaoDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}

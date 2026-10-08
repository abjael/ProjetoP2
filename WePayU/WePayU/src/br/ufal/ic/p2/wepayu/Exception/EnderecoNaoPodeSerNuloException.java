package br.ufal.ic.p2.wepayu.exception;

public class EnderecoNaoPodeSerNuloException extends ErroWePayUException {
    public EnderecoNaoPodeSerNuloException() {
        super("Endereco nao pode ser nulo.");
    }
}

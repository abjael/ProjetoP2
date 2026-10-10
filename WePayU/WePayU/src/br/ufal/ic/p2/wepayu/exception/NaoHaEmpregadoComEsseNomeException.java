package br.ufal.ic.p2.wepayu.exception;

public class NaoHaEmpregadoComEsseNomeException extends ErroWePayUException {
    public NaoHaEmpregadoComEsseNomeException() {
        super("Nao ha empregado com esse nome.");
    }
}

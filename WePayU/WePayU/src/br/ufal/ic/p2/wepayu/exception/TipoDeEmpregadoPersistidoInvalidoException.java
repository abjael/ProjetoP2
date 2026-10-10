package br.ufal.ic.p2.wepayu.exception;

public class TipoDeEmpregadoPersistidoInvalidoException extends ErroWePayUException {
    public TipoDeEmpregadoPersistidoInvalidoException(String tipo) {
        super("Tipo de empregado invalido nos dados persistidos: " + tipo);
    }
}
package br.ufal.ic.p2.wepayu.Exception;

public class TipoDeEmpregadoPersistidoInvalidoException extends Exception {
    public TipoDeEmpregadoPersistidoInvalidoException(String tipo) {
        super("Tipo de empregado invalido nos dados persistidos: " + tipo);
    }
}
package br.ufal.ic.p2.wepayu.exception;

public class SalarioDeveSerNumericoException extends ErroWePayUException {
    public SalarioDeveSerNumericoException() {
        super("Salario deve ser numerico.");
    }
}

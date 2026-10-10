package br.ufal.ic.p2.wepayu.exception;

public class EmpregadoNaoExisteException extends ErroWePayUException{
    public EmpregadoNaoExisteException(){
        super("Empregado nao existe.");
    }
}

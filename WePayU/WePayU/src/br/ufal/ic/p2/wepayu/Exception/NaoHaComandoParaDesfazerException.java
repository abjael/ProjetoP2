package br.ufal.ic.p2.wepayu.exception;

public class NaoHaComandoParaDesfazerException extends ErroWePayUException {
    public NaoHaComandoParaDesfazerException() {
        super("Nao ha comando a desfazer.");
    }
}

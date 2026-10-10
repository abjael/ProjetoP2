package br.ufal.ic.p2.wepayu.exception;

public class NaoHaComandoParaRefazerException extends ErroWePayUException {
    public NaoHaComandoParaRefazerException() {
        super("Nao ha comando a refazer.");
    }
}

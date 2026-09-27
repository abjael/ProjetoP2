package br.ufal.ic.p2.wepayu.Exception;

public class NaoHaComandoParaDesfazer extends Exception {
    public NaoHaComandoParaDesfazer() {
        super("Nao ha comando a desfazer.");
    }
}

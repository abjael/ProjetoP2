package br.ufal.ic.p2.wepayu.Exception;

public class NaoHaComandoParaRefazer extends Exception {
    public NaoHaComandoParaRefazer() {
        super("Nao ha comando a refazer.");
    }
}

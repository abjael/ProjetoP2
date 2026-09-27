package br.ufal.ic.p2.wepayu.Exception;

public class SistemEncerrado extends Exception {
    public SistemEncerrado() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}

package br.ufal.ic.p2.wepayu.Exception;

public class ErroAoCarregarDadosException extends Exception {
    public ErroAoCarregarDadosException() {
        super("Erro ao carregar os dados do WePayU.");
    }
}
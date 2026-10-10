package br.ufal.ic.p2.wepayu.exception;

public class ErroAoCarregarDadosException extends ErroWePayUException {
    public ErroAoCarregarDadosException() {
        super("Erro ao carregar os dados do WePayU.");
    }

    public ErroAoCarregarDadosException(Throwable causa) {
        super("Erro ao carregar os dados do WePayU.", causa);
    }
}

package br.ufal.ic.p2.wepayu.exception;

public class ErroAoSalvarDadosException extends ErroWePayUException {
    public ErroAoSalvarDadosException() {
        super("Erro ao salvar os dados do WePayU.");
    }

    public ErroAoSalvarDadosException(Throwable causa) {
        super("Erro ao salvar os dados do WePayU.", causa);
    }
}

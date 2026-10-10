package br.ufal.ic.p2.wepayu.exception;

public class ErroAoGerarArquivoDeFolhaException extends ErroWePayUException {
    public ErroAoGerarArquivoDeFolhaException() {
        super("Erro ao gerar arquivo de folha. ");
    }

    public ErroAoGerarArquivoDeFolhaException(Throwable causa) {
        super("Erro ao gerar arquivo de folha. ", causa);
    }
}

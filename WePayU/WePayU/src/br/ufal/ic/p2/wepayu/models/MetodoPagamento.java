package br.ufal.ic.p2.wepayu.models;

public enum MetodoPagamento {
    EM_MAOS("emMaos"),
    BANCO("banco"),
    CORREIOS("correios");

    private final String texto;

    MetodoPagamento(String texto) {
        this.texto = texto;
    }

    public static MetodoPagamento deTexto(String texto) {
        for (MetodoPagamento metodo : values()) {
            if (metodo.texto.equals(texto)) {
                return metodo;
            }
        }
        throw new IllegalArgumentException();
    }

    public String paraTexto() {
        return texto;
    }
}

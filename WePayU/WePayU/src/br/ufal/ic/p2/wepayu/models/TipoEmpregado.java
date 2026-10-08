package br.ufal.ic.p2.wepayu.models;

public enum TipoEmpregado {
    HORISTA,
    ASSALARIADO,
    COMISSIONADO;

    public static TipoEmpregado deTexto(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException();
        }

        switch (tipo) {
            case "horista":
                return HORISTA;
            case "assalariado":
                return ASSALARIADO;
            case "comissionado":
                return COMISSIONADO;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String paraTexto() {
        return switch (this) {
            case HORISTA -> "horista";
            case ASSALARIADO -> "assalariado";
            case COMISSIONADO -> "comissionado";
        };
    }
}

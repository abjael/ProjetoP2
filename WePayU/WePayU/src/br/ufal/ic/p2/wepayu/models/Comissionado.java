package br.ufal.ic.p2.wepayu.models;

public class Comissionado extends Empregado {

    private double comissao;

    public Comissionado(String id, String nome, String endereco, String tipo, double salario, double comissao, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
        this.comissao = comissao;
    }

    @Override
    public double getComissao() {
        return this.comissao;
    }
}
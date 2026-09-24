package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.List;

public class Comissionado extends Empregado {
    private double comissao;
    private List<Venda> vendas;

    public Comissionado(String id, String nome, String endereco, String tipo, double salario, double comissao, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
        this.comissao = comissao;
        this.vendas = new ArrayList<>();
    }

    @Override
    public double getComissao() {
        return comissao;
    }

    public void adicionarVenda(String data, double valor) {
        this.vendas.add(new Venda(data, valor));
    }

    public List<Venda> getVendas() {
        return vendas;
    }
    public void setComissao(double comissao) {
        this.comissao = comissao;
    }
}